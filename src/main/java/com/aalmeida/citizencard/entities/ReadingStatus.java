/*
 * Copyright (c) 2015 Alexandre Almeida.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aalmeida.citizencard.entities;

/**
 * The Class ReadingStatus.
 *
 * @author Alexandre
 */
public class ReadingStatus {
    
    public enum Status {
        UNKNOW_ERROR(1101),
        
        CHECK_IF_CARD_CORRECT_INSERTED(1104),
        
        NOT_CC_CARD(1210),
        
        ERROR(0),
        
        READING(1),
        
        READ(2);
        
        private int errorCode;
        
        /**
         * Instantiates a new status.
         *
         * @param pErrorCode
         *            the error code
         */
        Status(int pErrorCode) {
            errorCode = pErrorCode;
        }
        
        /**
         * Gets the error code.
         *
         * @return the error code
         */
        public int getErrorCode() {
            return errorCode;
        }
    }

}
