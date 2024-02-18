Web-Crawler Design
==================
This is a simple web-crawler that can be used to crawl a website and find all the links in the website. The web-crawler is implemented in java and uses the JSoup library to parse the HTML content of the website.

Components
------
The web-crawler is implemented using the following classes:
1. **Client** : This is the main class that maintains thread pool for crawling and printing jobs. It takes the URL of the website as input and passes it onto the queue manager to add it to the list of links to be crawled.
2. **QueueManager**: This class is responsible for managing the queues of links to be crawled and elements to be printed. It has utility methods to add and remove elements from two existing queues.
3. **Crawler**: This is the class that crawls the website. It polls the queue for links to be crawled. It uses the JSoup library to parse the HTML content of the website and finds all the links in the website. 
4. **Link**: This is a simple class that is used to represent a link in the website. It has a root URL and it's children.
5. **Printer**: This class is responsible for printing the link objects passed to it. It polls the queue for links to be printed and prints the `toString()` implementation of model class.

Flow
------
![img_1.png](img_1.png)

Design
------

![img.png](img.png)

Design Decisions
----------------
1. **Thread Pool**: The client class maintains a thread pool for crawling and printing jobs. This is done to ensure that the crawling and printing jobs are done in parallel and the client class can continue to add more links to the queue while the crawling and printing jobs are being done.
2. **Queues**: The queue manager class maintains two queues, one for links to be crawled and one for links to be printed. This is done to ensure that the client class can continue to add more links to the queue while the crawling and printing jobs are being done. This helps implement separation of concerns and makes the code more modular. 
3. **Data Structure**: Concurrent data structures are being used where multiple threads are accessing same object. 
    - **ConcurrentHashMap** is used to store the visited links. 
   ```java
    Set<String> visitedLinks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    ```
    - **ConcurrentLinkedQueue** are responsible for storing crawling requests and the printing request. Links are added and removed from the queue in a thread-safe manner.
    ```java
    this.crawlQueue =  new ConcurrentLinkedQueue<>();
    this.printQueue = new ConcurrentLinkedQueue<>();
   ```
4. The project uses logging framework to redirect the logs to two separate files. One for the crawling logs and one for the printing logs. This is done to ensure that the printer logs and the crawling logs are separated and can be easily managed.

Tradeoffs
---------
1. The project is built using native java threads and concurrent data structures. This is done to keep the project simple and avoid the overhead of using a framework like Spring. However, this means that the project is not scalable and cannot be used to crawl large websites.
However, in order to make application scalable, event driven would be a suitable approach. **_In memory queues would be replaced by a robust message broker like Kafka or RabbitMQ_.**
2. The project is not using any persistent data storage to check point the crawling process. This means that if the crawling process is interrupted, the entire process will have to be restarted from the beginning. Ideally, there should be a mechanism to keep a track of visited links in a **_persistent storage/ cache for application to recover and resume from the last checkpoint._**

All these decisions were made to keep the flow simple and to avoid the overhead of using a framework like Spring. However, these decisions can be revisited if the project is to be used in a production environment.