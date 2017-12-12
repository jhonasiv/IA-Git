package gui;

import java.util.ArrayDeque;

public class News
{
	private ArrayDeque<String> latestNews = new ArrayDeque<String>();
	
	public void addNews(String news)
	{
		latestNews.addFirst(news);
		if(latestNews.size() > 5)
		{
			latestNews.removeLast();
		}
	}
	
	public String feed()
	{
		String result = new String();
		for(int i = 0; i < latestNews.size(); i++)
		{
			result += latestNews.toArray()[i] + "\n";
		}
		return result;
	}
}
