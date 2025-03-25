package net.felsstudio.fenix.utils;

public class Timer {
    private long lastTime = -1;

    public Timer() {
        reset();
    }

    /**
     * Сбрасывает таймер, устанавливая текущее время как начальное.
     */
    public void reset() {
        lastTime = System.currentTimeMillis();
    }

    /**
     * Проверяет, прошло ли указанное количество миллисекунд с момента последнего сброса.
     *
     * @param milliseconds Количество миллисекунд для проверки.
     * @return true, если прошло больше или равно указанного времени, иначе false.
     */
    public boolean passed(long milliseconds) {
        return System.currentTimeMillis() - lastTime >= milliseconds;
    }

    /**
     * Возвращает количество миллисекунд, прошедших с момента последнего сброса.
     *
     * @return Количество миллисекунд.
     */
    public long getPassedTime() {
        return System.currentTimeMillis() - lastTime;
    }

    /**
     * Проверяет, прошло ли указанное количество секунд с момента последнего сброса.
     *
     * @param seconds Количество секунд для проверки.
     * @return true, если прошло больше или равно указанного времени, иначе false.
     */
    public boolean passedSeconds(double seconds) {
        return passed((long) (seconds * 1000L));
    }

    /**
     * Проверяет, прошло ли указанное количество минут с момента последнего сброса.
     *
     * @param minutes Количество минут для проверки.
     * @return true, если прошло больше или равно указанного времени, иначе false.
     */
    public boolean passedMinutes(double minutes) {
        return passed((long) (minutes * 1000L * 60L));
    }
}