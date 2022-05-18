package badda3mon.lockscreen.models;

public class Problem {
	private int mFirstNumber;
	private int mSecondNumber;
	private String mOperationMark;
	private String mProblemString;
	private String mProblemStringWithEqualsMark;

	private int mRightAnswer;

	public Problem(int first, int second, String operationMark, int rightAnswer){
		mFirstNumber = first;
		mSecondNumber = second;

		mOperationMark = operationMark;

		mProblemString = first + " " + mOperationMark + " " + second;
		mProblemStringWithEqualsMark = mProblemString + " = ";

		mRightAnswer = rightAnswer;
	}

	public int getFirstNumber(){
		return mFirstNumber;
	}

	public int getSecondNumber(){
		return mSecondNumber;
	}

	public String getOperationMark(){
		return mOperationMark;
	}

	public String getProblemString(){
		return mProblemString;
	}

	public String getProblemStringWithEqualsMark(){
		return mProblemStringWithEqualsMark;
	}

	public int getRightAnswer(){
		return mRightAnswer;
	}
}
