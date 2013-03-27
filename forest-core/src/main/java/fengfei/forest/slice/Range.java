package fengfei.forest.slice;

public class Range {

	public Long start;
	public Long end;

	public Range(long start, long end) {
		this(new Long(start), new Long(end));
	}

	public Range(Long start, Long end) {
		super();
		this.start = start;
		this.end = end;
		if (end != null && start != null && end < start) {
			throw new IllegalArgumentException("Are you sure end > start?");
		}
	}
}