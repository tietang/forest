package fengfei.forest.slice.equalizer;

import java.util.Calendar;
import java.util.Date;

import fengfei.forest.slice.Equalizer;

public class DateEqualizer implements Equalizer<Date> {
	private boolean isYear = true;
	private boolean isMonth = false;
	private boolean isDay = false;

	public DateEqualizer() {
	}

	public DateEqualizer(boolean isYear, boolean isMonth, boolean isDay) {
		super();
		this.isYear = isYear;
		this.isMonth = isMonth;
		this.isDay = isDay;
	}

	@Override
	public long get(Date key, int sliceSize) {
		Calendar c = Calendar.getInstance();
		c.setTime(key);
		StringBuilder sb = new StringBuilder();
		if (isYear) {
			sb.append(c.get(Calendar.YEAR));
		}
		if (isMonth) {
			int m = c.get(Calendar.MONTH);
			sb.append(m < 10 ? "0" : "");
			sb.append(m);
		}
		if (isDay) {

			int d = c.get(c.get(Calendar.DAY_OF_MONTH));
			sb.append(d < 10 ? "0" : "");
			sb.append(d);
		}

		return Long.parseLong(sb.toString());
	}

	public static void main(String[] args) {
		int sliceSize = 1000;
		DateEqualizer equalizer = new DateEqualizer(true, true, false);
		Calendar c = Calendar.getInstance();
//		c.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < 100; i++) {
			c.add(Calendar.MONTH, 1);
			long in = equalizer.get(c.getTime(), sliceSize);
			System.out.printf("%s %d \n", c.getTime().toLocaleString(), in);
		}
	}
}
