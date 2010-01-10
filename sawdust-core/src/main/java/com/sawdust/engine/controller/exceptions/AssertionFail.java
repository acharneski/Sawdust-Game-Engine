package com.sawdust.engine.controller.exceptions;

public class AssertionFail extends SawdustSystemError
{

    /**
	 * 
	 */
    public AssertionFail()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public AssertionFail(final String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public AssertionFail(final String message, final Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public AssertionFail(final Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
