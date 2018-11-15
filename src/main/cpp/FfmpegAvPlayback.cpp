#include "FfmpegAvPlayback.h"
#include "MediaPlayerErrors.h"

bool FfmpegAvPlayback::kEnableShowStatus = true;

void FfmpegAvPlayback::TogglePause() {

  // Get all the clocks
	Clock *pExtclk = nullptr;
	Clock *pVidclk = nullptr;
	Clock *pAudclk = nullptr;
	p_video_state_->GetExternalClock(&pExtclk);
	p_video_state_->GetImageClock(&pVidclk);
	p_video_state_->GetAudioClock(&pAudclk);

  // Update the video clock
  if (p_video_state_->IsPaused()) {
    SetFrameLastShownTime(GetFrameLastShownTime() +
                          av_gettime_relative() / 1000000.0 -
                          p_video_state_->GetImageClockLastSetTime());
    pVidclk->SetTime(pVidclk->GetTime(), pVidclk->GetSerial());
  }
  // Update the external clock
  pExtclk->SetTime(pExtclk->GetTime(), pExtclk->GetSerial());

  // Flip the paused flag
  p_video_state_->SetPaused(!p_video_state_->IsPaused());
}

FfmpegAvPlayback::FfmpegAvPlayback()
    : p_video_state_(nullptr), display_disabled_(false), frame_width_(0), frame_height_(0),
      force_refresh_(1), num_frame_drops_late_(0) {}

int FfmpegAvPlayback::OpenVideo(const char *filename, AVInputFormat *p_input_format,
                                int audio_buffer_size) {
  return VideoState::OpenStream(&p_video_state_, filename, p_input_format,
                                audio_buffer_size);
}

void FfmpegAvPlayback::SetPlayerStateCallbackFunction(
    VideoState::PlayerStateCallback callback,
    const std::function<void()> &func) {
  p_video_state_->SetPlayerStateCallbackFunction(callback, func);
}

void FfmpegAvPlayback::Play() {
  if (p_video_state_->IsPaused()) {
    TogglePauseAndStopStep();
    p_video_state_->SetStopped(false);
  }
}

// Stop and put the playback speed to 0x
// Note the playback speed is not implemetnted yet
void FfmpegAvPlayback::Stop() {
  if (!p_video_state_->IsPaused()) {
    TogglePauseAndStopStep();
    p_video_state_->SetStopped(true);
  }
}

// pause and keep the playback speed.
// Note the playback speed is not implemetnted yet
void FfmpegAvPlayback::TogglePauseAndStopStep() {
  TogglePause();
  p_video_state_->SetStepping(false);
}

int FfmpegAvPlayback::SetSpeed(double speed) {
  int err = p_video_state_->SetSpeed(speed);
  return err ? ERROR_FFMPEG_FILTER_NOT_FOUND : ERROR_NONE;
}

double FfmpegAvPlayback::ComputeFrameDuration(Frame *vp, Frame *nextvp,
                                              double max_frame_duration) {
  if (vp->serial_ == nextvp->serial_) {
    double duration = (nextvp->pts_ - vp->pts_) / p_video_state_->GetSpeed();
    if (isnan(duration) || duration <= 0 || duration > max_frame_duration)
      return vp->duration_;
    else
      return duration;
  } else {
    return 0.0;
  }
}
