package com.taobao.freeproj.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import tools.PagedList;

import com.taobao.freeproj.domain.Counter;
import com.taobao.freeproj.domain.Timestamp;
import com.taobao.freeproj.orm.AbstractDao;
import com.taobao.freeproj.orm.Session;

public class CounterDaoImpl extends AbstractDao<Counter> {
	static boolean isValidKey(String key) {
		if (tools.StringUtil.isNullOrEmpty(key))
			return false;

		char[] arr = key.toCharArray();
		for (char ch : arr) {
			// ����������ĸ��_��%��.������
			if (!Character.isLetter(ch) && ch != '_' && ch != '.' && ch != '%' && !Character.isDigit(ch))
				return false;
		}

		return true;
	}

	public int save(List<Counter> list) {
		return super.insertBatch(list);
	}

	/**
	 * Ŀǰ����reservedValue 2013-08-14 by liusan.dyf<br />
	 * 
	 * @TODO �𲽿��Ƿ��� 2015-4-9 20:01:08 by ����
	 * @param m
	 */
	private final void fixValueInMap(Map<String, Object> m) {
		if (m == null)
			return;

		// ָ���������޸�query����
		String reservedKey = "reservedValue";
		if (m != null && m.containsKey(reservedKey)) {
			String reservedValue = tools.Convert.toString(m.get(reservedKey));

			// ��֤ǩ����ʹ��md5ǩ���������ȷ���͵�������
			reservedValue = tools.StringUtil.getRawContent(reservedValue, "freeproj");
			if (tools.Validate.isNullOrEmpty(reservedValue))
				m.remove(reservedKey);// ���Ϸ�
			else
				m.put(reservedKey, reservedValue);// �滻Ϊ�Ϸ���
		}
	}

	/**
	 * Ϊ����counter�ֱ� 2013-05-02 by liusan.dyf
	 */
	@Override
	public PagedList<Counter> getPagedList(Map<String, Object> query, int pageIndex, int pageSize) {
		fixValueInMap(query);

		return super.getPagedList(query, pageIndex, pageSize);
	}

	/************ ʱ��� 2012-08-10 ****************/

	public void insertTimestampList(List<Timestamp> list) {
		Session session = super.openSession(true, 0, false);
		try {
			// ��������
			// TODO ibatis��mybatis��ʵ�֣�����᲻ͬ

			for (Timestamp item : list) {
				session.insert("insertTimestamp", item);
			}

			session.commit();
		} finally {
			session.close();
		}
	}

	public void insertTimestamp(long start, long end) {
		// �����Ӷ���
		long m = start % (60 * 1);
		start = start - m;

		List<Timestamp> list = new ArrayList<Timestamp>();
		for (long i = start; i <= end; i = i + 60) {
			Timestamp t = new Timestamp();

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(i * 1000);

			t.setTimestamp(i);

			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int min = calendar.get(Calendar.MINUTE);
			int sec = calendar.get(Calendar.SECOND);

			t.setYear(year);
			t.setMonth(month);
			t.setDay(day);
			t.setHour(hour);
			t.setMinute(min);
			t.setSecond(sec);

			// 2012-03-02
			t.setYearMonth(year * 100 + month);
			t.setYearMonthDay(year * 10000 + month * 100 + day);
			t.setYearMonthDayHour(year * 1000000 + month * 10000 + day * 100 + hour);

			list.add(t);

			if (list.size() >= 1000) {
				insertTimestampList(list);
				list.clear();
			}
		}// end for

		// ʣ�µ�ҲҪ����
		insertTimestampList(list);
	}

	@Override
	public String getNamespace() {
		return "counter";
	}
}
