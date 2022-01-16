package com.won983212.rewind.recorder;

public class RecordingFailedException extends RuntimeException {

    public RecordingFailedException() {
        super();
    }

    public RecordingFailedException(String s) {
        super(s);
    }

    public RecordingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordingFailedException(Throwable cause) {
        super(cause);
    }
}
