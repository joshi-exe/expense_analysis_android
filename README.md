No additional configuration is required to run the code, simple build and run.

Currently I have limited the size of both Income and Expensed list to "5", so that the list isn't flooded if there are way too many messages.

To remove the limit, follow the below steps:

1. At line 183, remove the "&& income.size < 5" part from if condition.
2. Similarly, at line 215, remove "&& expense.size < 5" from if condition.
3. At line 224, "&& !isLimitReached" remove from while condition.

Whole project is structured properly into packages and organized with proper naming conventions, hence can be traversed
to find things without any documentation.

Please find the below details for attachments:
1. I'll drop the build apk inside "apk" folder in the root directory of the project (build: debug apk).

Find below steps to navigate around the app:
1. There is an initial alert dialog on app launch to alert about limit size for array list. It's configured to not be cancellable on back press or tap outside. So 
press ok to exit out of it.
2. Initially all the messages will be analysed on app launch. If there are too many messages I have added a progress bar. In case there are less message the list will 
populate immediately.
3. Initially when the list populates, the option to edit tags will be enabled. Enter a valid tag (not empty) and press submit button to update a tag against a message.
4. Once a tag is updated for a message the edit field will be hidden and so will be submit button. And the updated tag will be visible immediately.
5. Messages can be searched with their tags. It'll be searchable only after a tag is set. Find the search button on top action bar. Tap on magnifying glass and type a custom 
tag that is set for a message. List will be filtered.
6. Tag on graph icon on top right corner of action bar to load bar graph against the tags. NOTE: at least one tag need to be set to see graph according to my conditions. 
To remove such condition, remove the else-if condition at line 79. The graph is also only and only accessible if any list is populated. If in case no transaction messages are 
found the list will be empty and I haven't allowed in the app to redirect to graph activity.
7. Bar Graph can be zoomed in and panned around. It's an interactable graph.

Feel free to contact me if there are any queries, I'll drop my details below:

email: 69yash@gmail.com
