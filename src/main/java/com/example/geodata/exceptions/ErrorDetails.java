package com.example.geodata.exceptions;


import java.util.Date;

public record ErrorDetails(Date date, String message, String details) { }
