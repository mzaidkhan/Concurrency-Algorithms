package lab.exercise.two.two;

public interface Lock {
    public void requestCS(int id);
    public void releaseCS(int id);
}