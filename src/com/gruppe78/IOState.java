public class IOState{
    public int elevFloor;
    public int[] buttonDown;
    public int[] buttonUp;
    public int[] buttonInternal;
    public boolean stopButton;
    public IOState(int numFloor){
        buttonDown = new int [numFloor];
        buttonUp = new int [numFloor];
        buttonInternal = new int [numFloor];
    }
}