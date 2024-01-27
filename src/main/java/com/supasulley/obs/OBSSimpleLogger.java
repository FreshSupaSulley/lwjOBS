package com.supasulley.obs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

@SuppressWarnings("deprecation")
class OBSSimpleLogger extends MarkerIgnoringBase {
	
	private static final long serialVersionUID = 6336574372502481315L;
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
	
	@Override
	public boolean isInfoEnabled()
	{
		return true;
	}
	
	@Override
	public void info(String msg)
	{
		System.out.println(getPrefix() + msg);
	}
	
	@Override
	public void info(String format, Object arg)
	{
		System.out.println(getPrefix() + MessageFormatter.format(format, arg).getMessage());
	}
	
	@Override
	public void info(String format, Object arg1, Object arg2)
	{
		System.out.println(getPrefix() + MessageFormatter.format(format, arg1, arg2).getMessage());
	}
	
	@Override
	public void info(String format, Object... arguments)
	{
		System.out.println(getPrefix() + MessageFormatter.format(format, arguments).getMessage());
	}
	
	@Override
	public void info(String msg, Throwable t)
	{
		System.out.println(getPrefix() + printThrowable(msg, t));
	}
	
	@Override
	public boolean isErrorEnabled()
	{
		return true;
	}
	
	@Override
	public void error(String msg)
	{
		System.err.println(getPrefix() + msg);
	}
	
	@Override
	public void error(String format, Object arg)
	{
		System.err.println(getPrefix() + MessageFormatter.format(format, arg).getMessage());
	}
	
	@Override
	public void error(String format, Object arg1, Object arg2)
	{
		System.err.println(getPrefix() + MessageFormatter.format(format, arg1, arg2).getMessage());
	}
	
	@Override
	public void error(String format, Object... arguments)
	{
		System.err.println(getPrefix() + MessageFormatter.format(format, arguments).getMessage());
	}
	
	@Override
	public void error(String msg, Throwable t)
	{
		System.err.println(getPrefix() + printThrowable(msg, t));
	}
	
	private String printThrowable(String message, Throwable t)
	{
		return message + "\n" + getStackTrace(t);
	}
	
	private String getStackTrace(final Throwable throwable)
	{
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString().strip();
	}
	
	private String getPrefix()
	{
		return "[lwjOBS " + dateFormatter.format(new Date()) + "] - ";
	}
	
	// Not doing these for a simple implementation
	@Override
	public boolean isWarnEnabled()
	{
		return false;
	}
	
	public void warn(String msg) {}
	public void warn(String format, Object arg) {}
	public void warn(String format, Object... arguments) {}
	public void warn(String format, Object arg1, Object arg2){}
	public void warn(String msg, Throwable t) {}

	@Override
	public boolean isTraceEnabled()
	{
		return false;
	}
	
	public void trace(String msg) {}
	public void trace(String format, Object arg) {}
	public void trace(String format, Object arg1, Object arg2) {}
	public void trace(String format, Object... arguments) {}
	public void trace(String msg, Throwable t) {}
	
	@Override
	public boolean isDebugEnabled()
	{
		return false;
	}
	
	public void debug(String msg) {}
	public void debug(String format, Object arg) {}
	public void debug(String format, Object arg1, Object arg2) {}
	public void debug(String format, Object... arguments) {}
	public void debug(String msg, Throwable t) {}
}
