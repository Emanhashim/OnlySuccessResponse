package com.bazra.usermanagement.service;

import org.springframework.stereotype.Service;

@Service
public class RandomNumber {
	public int randomNumberGenerator(int min, int max)
	{
		double r = Math.random();
		int randomNum = (int)(r * (max - min)) + min;
		return randomNum;
	}
}
