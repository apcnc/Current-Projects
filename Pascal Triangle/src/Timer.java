

public class Timer {
	private long startTime;
	private long endTime;
	private long passedTime;
	
	public void start(){
		startTime = System.currentTimeMillis();
	}
	
	public void end(){
		endTime = System.currentTimeMillis();
		passedTime = endTime-startTime;
	}
	
	/**
	 * 
	 * @param name "Total execution time "+name+":" + passedTime
	 */
	public void printTime(String name){
		System.out.println("Total execution time "+name+":" + passedTime);
	}
	
	public long getPassedTime() {
		return passedTime;
	}
}
