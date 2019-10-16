package com.itheima.first;

import java.util.Scanner;

public class HelloWorld {
	public static void main(String[] args) {
		Scanner s=new Scanner(System.in);
		int count;
		count=0;
		int [][] arr=new int [4][4];
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				arr[i][j]=s.nextInt();
				count++;
			}
		}
		s.close();
		int x,y,m;
		x=y=m=0;//x为外围元素之和，y为主对角线，m为副对角线
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				if((i==0||i==3)||(j==0||j==3))
				{
					x+=arr[i][j];
				}
				 if(i==j)
				{
					y+=arr[i][j];
				}
				if(3==j+i)
				{
					m+=arr[i][j];
				}
			}
		}
		System.out.println("外围元素之和为："+x);
		System.out.println("主对角元素之和为："+y);
		System.out.println("副对角元素之和为："+m);
	}
}
