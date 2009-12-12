/**
 * 
 */
package com.sawdust.engine.common.cards;

public enum SpecialCard
{
    FaceDown
    {
        @Override
        public String toString()
        {
            return "VR";
        }
    },
    Null
    {
        @Override
        public String toString()
        {
            return null;
        }
    },
}
