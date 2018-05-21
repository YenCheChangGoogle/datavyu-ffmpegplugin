#include "Logger.h"
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <algorithm>
#include <sstream>
#include <stdlib.h>

#ifndef LOGIC_FOR_BUFFER_H_
#define LOGIC_FOR_BUFFER_H_

template<class Item>
class LogicForBuffer {
    int nItem; // Capacity of the buffer
    int iRead; // Read pointer
    int iWrite; // Write pointer
    int nWrite; // Number of written items
    int nRead; // Number of read items
    std::mutex mu;
    std::condition_variable cv;
    std::atomic<bool> unblocking;
    void reset() {
        iRead = 0;
        iWrite = 0;
        nWrite = 0;
        nRead = 0;
        unblocking = false;
    }
protected:
    Item* buffer;
public:
    LogicForBuffer(int nItem) : buffer(nullptr), nItem(nItem) {
        reset();
    }
    inline bool empty() const { return nWrite == 0; }
    inline int size() const { return nItem; }
    inline int nFree() { return nItem - nWrite; }
    virtual void log(Logger& pLogger) {
        pLogger.info("iRead = %d, iWrite = %d, nWrite = %d.", iRead, iWrite, nWrite);
    }
    void peekLast(Item* item) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nWrite > 0 || unblocking;});
        *item = unblocking ? nullptr : buffer[(iRead-nRead+nItem) % nItem];
		locker.unlock();
		cv.notify_all();
    }
    void peekCurrent(Item *item) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nWrite > 0 || unblocking;});
        *item = unblocking ? nullptr : buffer[iRead];
		locker.unlock();
		cv.notify_all();
    }
    void peekNext(Item *item) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nWrite > 1 || unblocking;});
        *item = unblocking ? nullptr : buffer[(iRead+1) % nItem];
		locker.unlock();
		cv.notify_all();
    }
    void writeRequest(Item* item) {
        std::unique_lock<std::mutex> locker(mu);
        // Always leave two spots open because it could be currently where the read pointer or when we would like to
        // peek last
        cv.wait(locker, [this](){ return nFree() > 1 || unblocking; });
        *item = unblocking ? nullptr : buffer[iWrite];
		locker.unlock();
		cv.notify_all();
    }
    void writeComplete() {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){ return nFree() > 1 || unblocking; });
        if (!unblocking) {
            nWrite++;
            iWrite = (iWrite + 1) % nItem;
        }
		locker.unlock();
		cv.notify_all();
    }
    void next() {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return nWrite > 0 || unblocking;});
        if (!unblocking) {
            iRead = (iRead + 1) % nItem;
            nRead = 1;
            nWrite--;
        }
		locker.unlock();
		cv.notify_all();
    }
    void unblock() {
        unblocking = true;
        cv.notify_all();
    }
    void block() {
        unblocking = false;
    }
    void flush() {
        unblocking = true;
        cv.notify_all();
        std::unique_lock<std::mutex> locker(mu);
        reset();
        locker.unlock();
        unblocking = false;
    }
};

#endif #LOGIC_FOR_BUFFER_H_
