/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "Pipeline.h"
#include "JavaPlayerEventDispatcher.h"
#include "MediaPlayerErrors.h"

CPipeline::CPipeline(CPipelineOptions *p_options)
    : p_event_dispatcher_(nullptr), p_options_(p_options),
      player_state_(Unknown), player_pending_state_(Unknown) {}

CPipeline::~CPipeline() {
  if (nullptr != p_options_) {
    delete p_options_;
  }

  Dispose();

  if (nullptr != p_event_dispatcher_) {
    delete p_event_dispatcher_;
  }
}

void CPipeline::SetEventDispatcher(
    CJavaPlayerEventDispatcher *pEventDispatcher) {
  p_event_dispatcher_ = pEventDispatcher;
}

void CPipeline::Dispose() {}

void CPipeline::UpdatePlayerState(PlayerState new_state) {
  PlayerState newPlayerState =
      player_state_; // If we assign the same state again
  bool bSilent = false;

  switch (player_state_) {
  case Unknown:
    if (Ready == new_state) {
      newPlayerState = Ready;
    }
    break;

  case Ready:
    if (Playing == new_state) {
      newPlayerState = Playing;
    }
    break;

  case Playing:
    if (Stalled == new_state || Paused == new_state || Stopped == new_state ||
        Finished == new_state) {
      newPlayerState = new_state;
    }
    break;

  case Paused:
    if (Stopped == new_state || Playing == new_state) {
      newPlayerState = new_state;
    }
    break;

  case Stopped:
    if (Playing == new_state || Paused == new_state) {
      newPlayerState = new_state;
    }
    break;

  case Stalled: {
    if (Stopped == new_state || Paused == new_state || Playing == new_state) {
      newPlayerState = new_state;
    }
    break;
  }

  case Finished:
    if (Playing == new_state) {
      // We can go from Finished to Playing only when seek happens (or repeat)
      // This state change should be silent.
      newPlayerState = Playing;
      bSilent = true;
    }
    if (Stopped == new_state) {
      newPlayerState = Stopped;
    }

    break;

  case Error:
    break;
  }

  // The same thread can acquire the same lock several times
  SetPlayerState(newPlayerState, bSilent);
}

void CPipeline::SetPlayerState(PlayerState new_state, bool silent) {

  // Determine if we need to send an event out
  bool updateState = new_state != player_state_;
  if (updateState) {
    if (nullptr != p_event_dispatcher_ && !silent) {
      player_state_ = new_state;

      if (!p_event_dispatcher_->SendPlayerStateEvent(new_state, 0)) {
        p_event_dispatcher_->SendPlayerMediaErrorEvent(
            ERROR_JNI_SEND_PLAYER_STATE_EVENT);
      }
    } else {
      player_state_ = new_state;
    }
  }
}

#ifdef SDL_ENABLED
void CPipeline::MapSdlToJavaKey(SDL_Keycode sdlKeyCode) {
  int javaCode = -1;
  // Map Numpad keys from SDL to Java keyEvent
  switch (sdlKeyCode) {
  case SDLK_KP_0:
    javaCode = 0x60;
    break;
  case SDLK_KP_1:
    javaCode = 0x61;
    break;
  case SDLK_KP_2:
    javaCode = 0x62;
    break;
  case SDLK_KP_3:
    javaCode = 0x63;
    break;
  case SDLK_KP_4:
    javaCode = 0x64;
    break;
  case SDLK_KP_5:
    javaCode = 0x65;
    break;
  case SDLK_KP_6:
    javaCode = 0x66;
    break;
  case SDLK_KP_7:
    javaCode = 0x67;
    break;
  case SDLK_KP_8:
    javaCode = 0x68;
    break;
  case SDLK_KP_9:
    javaCode = 0x69;
    break;
  case SDLK_KP_MULTIPLY:
    javaCode = 0x6A;
    break;
  case SDLK_KP_PLUS:
    javaCode = 0x6B;
    break;
  case SDLK_KP_DIVIDE:
    javaCode = 0x6F;
    break;
  case SDLK_KP_MINUS:
    javaCode = 0x2D;
    break;
  case SDLK_KP_ENTER:
    javaCode = '\n';
    break;
  case SDLK_KP_PERIOD:
    javaCode = 0x2E;
    break;
  }

  if (javaCode != -1)
    DispatchKeyEvent(javaCode);
}

void CPipeline::DispatchKeyEvent(int javaKeyCode) {
  if (nullptr != p_event_dispatcher_) {
    p_event_dispatcher_->SendSdlPlayerKeyEvent(javaKeyCode);
  }
}
#endif // SDL_ENABLED
