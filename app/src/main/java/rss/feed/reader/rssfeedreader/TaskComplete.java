/* **************************************************
Author: Vlad Zat
Description: Interface that the AsyncTasks use to
tell the main thread that it's finished or to send
data to it

Created: 2016/11/12
************************************************** */

package rss.feed.reader.rssfeedreader;

interface TaskComplete {
    public void callback();
    public void callback(String url);
}
