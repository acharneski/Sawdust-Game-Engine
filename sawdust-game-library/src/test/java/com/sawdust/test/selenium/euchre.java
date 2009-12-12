package com.sawdust.test.selenium;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class euchre extends SeleneseTestCase
{
    public void setUp() throws Exception
    {
        setUp("http://localhost:8080/", "*chrome");
    }
    
    public void testEuchre_1() throws Exception
    {
        selenium.open("/game.jsp?game=Euchre");
        
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Play Now!".equals(selenium.getText("//button[@type='button']"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if (selenium.isElementPresent("//input[@type='text']")) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        selenium.type("//input[@type='text']", "Test Euchre Game #1");
        selenium.type("//div[@id='gameCreator-main']/table/tbody/tr[1]/td/table/tbody/tr[4]/td[2]/table/tbody/tr/td[1]/input", "10/18/09 2:04 AM!");
        selenium.click("gwt-uid-1");
        selenium.click("gwt-uid-2");
        selenium.click("//div[@id='gameCreator-main']/table/tbody/tr[3]/td/button");
        selenium.waitForPageToLoad("300000");
        selenium.waitForPopUp("_top", "30000");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Add AI Player (Normal)".equals(selenium.getText("//div[@id='gwt-debug-button_Add AI 4']/button"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        selenium.mouseUp("//div[@id='gwt-debug-button_Add AI 4']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Add AI Player (Normal)".equals(selenium.getText("//div[@id='gwt-debug-button_Add AI 5']/button"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        selenium.mouseUp("//div[@id='gwt-debug-button_Add AI 5']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Add AI Player (Normal)".equals(selenium.getText("//div[@id='gwt-debug-button_Add AI 6']/button"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        selenium.mouseUp("//div[@id='gwt-debug-button_Add AI 6']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Queen of Diamonds".equals(selenium.getAttribute("gwt-debug-token_2@title"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        verifyEquals("King of Hearts", selenium.getAttribute("gwt-debug-token_3@title"));
        verifyEquals("King of Diamonds", selenium.getAttribute("gwt-debug-token_4@title"));
        verifyEquals("Nine of Spades", selenium.getAttribute("gwt-debug-token_5@title"));
        verifyEquals("Jack of Spades", selenium.getAttribute("gwt-debug-token_6@title"));
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Pass".equals(selenium.getText("//div[@id='gwt-debug-button_GeneralCommand 0']/button"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        verifyEquals("Call", selenium.getText("//div[@id='gwt-debug-button_GeneralCommand 1']/button"));
        verifyEquals("Pass", selenium.getText("//div[@id='gwt-debug-button_GeneralCommand 0']/button"));
        selenium.mouseUp("//div[@id='gwt-debug-button_GeneralCommand 0']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Spades".equals(selenium.getText("//div[@id='gwt-debug-button_GeneralCommand 8']/button"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        selenium.mouseUp("//div[@id='gwt-debug-button_GeneralCommand 8']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Played Seven of Diamonds".equals(selenium.getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[2]/td/div/span[2]")))
                    break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("It is now selenium's turn:".equals(selenium.getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[1]/td/div/span")))
                    break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Play".equals(selenium.getText("//div[@id='gwt-debug-button_PlayCard 4']/button"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        selenium.mouseUp("//div[@id='gwt-debug-button_PlayCard 4']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("It is now selenium's turn:".equals(selenium.getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[1]/td/div/span")))
                    break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        verifyEquals("selenium (team 1) wins this trick, for a total of 1 wins", selenium
                .getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[2]/td/div/strong"));
        verifyEquals("Play", selenium.getText("//div[@id='gwt-debug-button_PlayCard 3']/button"));
        selenium.mouseUp("//div[@id='gwt-debug-button_PlayCard 3']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("It is now selenium's turn:".equals(selenium.getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[1]/td/div/span")))
                    break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        verifyEquals("Play", selenium.getText("//div[@id='gwt-debug-button_PlayCard 1']/button"));
        selenium.mouseUp("//div[@id='gwt-debug-button_PlayCard 1']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("It is now selenium's turn:".equals(selenium.getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[1]/td/div/span")))
                    break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        verifyEquals("Team 1", selenium.getText("//div[@id='gwt-debug-label_TeamLabel 0']/div"));
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Team 1 wins the game!".equals(selenium.getText("//div[@id='cardTable']/table/tbody/tr[3]/td/div/table/tbody/tr[3]/td/div/strong"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
        verifyEquals("Deal Again", selenium.getText("//div[@id='gwt-debug-button_GeneralCommand 0']/button"));
        selenium.mouseUp("//div[@id='gwt-debug-button_GeneralCommand 0']/button");
        for (int second = 0;; second++)
        {
            if (second >= 60) fail("timeout");
            try
            {
                if ("Ten of Spades".equals(selenium.getAttribute("gwt-debug-token_22@title"))) break;
            }
            catch (Exception e)
            {
            }
            Thread.sleep(1000);
        }
        
    }
}
