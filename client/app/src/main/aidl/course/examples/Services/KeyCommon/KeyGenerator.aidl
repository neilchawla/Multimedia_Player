package course.examples.Services.KeyCommon;

interface KeyGenerator {
    void playAudio(int number);
    void pauseAudio();
    void stopAudio();

    Bitmap sendImage(int number);
}