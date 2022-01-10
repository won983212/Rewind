package com.won983212.rewind.client;

import com.won983212.rewind.CommonDist;
import com.won983212.rewind.recorder.Recorder;
import com.won983212.rewind.replayer.Replayer;

public class ClientDist extends CommonDist {
    public static final Recorder RECORDER = new Recorder();
    public static final Replayer REPLAYER = new Replayer();
}
