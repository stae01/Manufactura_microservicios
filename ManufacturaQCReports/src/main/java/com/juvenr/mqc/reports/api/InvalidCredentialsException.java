/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.juvenr.mqc.reports.api;

/**
 *
 * @author carli
 */
public class InvalidCredentialsException extends Exception {

    /**
     * Creates a new instance of <code>InvalidCredentialsException</code>
     * without detail message.
     */
    public InvalidCredentialsException() {
    }

    /**
     * Constructs an instance of <code>InvalidCredentialsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}
