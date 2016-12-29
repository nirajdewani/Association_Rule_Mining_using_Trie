public class Rule implements Comparable<Rule> {

	String rule;
	double confidence;
	double lift;
	double support;
	double leverage;
	
	public Rule(String rule, double confidence, double lift, double support, double leverage) {
		super();
		this.rule = rule;
		this.confidence = confidence;
		this.lift = lift;
		this.support = support;
		this.leverage = leverage;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public double getLift() {
		return lift;
	}

	public void setLift(double lift) {
		this.lift = lift;
	}

	public double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}
	
	public double getLeverage() {
		return leverage;
	}

	public void setLeverage(double leverage) {
		this.leverage = leverage;
	}

	@Override
	public int compareTo(Rule r1) {
		return new Double(r1.getConfidence()).compareTo(this.confidence);
	}
}
