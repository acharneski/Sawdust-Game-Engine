package com.sawdust.test.mock;

import com.sawdust.engine.controller.entities.Account;
import com.sawdust.engine.controller.entities.GameSession;
import com.sawdust.engine.controller.entities.SessionToken;
import com.sawdust.engine.controller.exceptions.GameException;

public class MockSessionToken implements SessionToken
{
	private final Account _account;
	private final GameSession session;
	private final String user;
	
	/**
	 * @param puser
	 * @param psession
	 */
	public MockSessionToken(String puser, GameSession psession)
	{
		super();
		this.user = puser;
		this.session = psession;
		_account = new MockAccount(puser); 
	}

	public String getUserId()
	{
		return user;
	}

	public Account doLoadAccount()
	{
		return _account;
	}

	public GameSession doLoadSession() throws GameException
	{
		return session;
	}

}
