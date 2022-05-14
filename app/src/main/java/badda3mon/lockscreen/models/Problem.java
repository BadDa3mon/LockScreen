package badda3mon.lockscreen.models;

public class Problem {
	private String mOperationMark;
	private String mProblemString;
	private String mProblemStringWithEqualsMark;

	private int mRightAnswer;

	public Problem(String operationMark, String problemString, int rightAnswer){
		mOperationMark = operationMark;

		mProblemString = problemString;
		mProblemStringWithEqualsMark = mProblemString + " = ";

		mRightAnswer = rightAnswer;
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
