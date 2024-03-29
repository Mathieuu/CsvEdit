CsvEdit
=======

CSV Editor plugin for Eclipse

## Status

Feel free to clone, edit, build and release (just update version and/or name of your fork, so make it clear for users)
or ask for push access here. Tell about new fork by adding new issue. I will happily put a link to the new repo.


## What is this ?

This repository is a fork of [https://code.google.com/p/csvedit/](https://code.google.com/p/csvedit/) created by Mathieu Savy in 2104. It contains the latest functionalities available for CSVEdit but most of the new features are still being tested. If you look for a more reliable version of the plugin you should probably visit the original repository.

- The previous stable release 1.1.3 was available [on Eclipse Marketplace](https://marketplace.eclipse.org/content/csv-edit).
- The version available in this repository is 1.2.x

The intention was to contribute features from here to the original repository once they are mature.

### How to get the latest version

HELP WANTED WITH PUBLISHING 1.2+ build

0. NOT AVAILABLE Via Eclipse marketplace at <https://marketplace.eclipse.org/content/csv-edit-resurrector>  
or drag-n-drop the image on the Eclipse toolbar  
[![Drag onto your running Eclipse toolbar. *Requires Eclipse Marketplace Client](images/resurrector-CSV-logo.png)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=3739198)

1. NOT AVAILABLE The latest version (1.2) 2017-10-31 built is NO LONGER available with update-site `http://www.nodeclipse.org/updates/csvedit/`. 
In Eclipse Help -> Install new software, add the URL and follow the instructions.

2. Or from this repository: 
[https://raw.githubusercontent.com/SegFaultError/CsvEdit/master/csvedit.update/site.xml](https://raw.githubusercontent.com/SegFaultError/CsvEdit/master/csvedit.update/site.xml)

- [] TODO 3. Offline site archive from GitHub releases.

4. Other option is to build from sources with maven `mvn package` and install from local repository archive, that will be in `csvedit.site/target/repository`.

### CSVEdit

CSVEdit make CSVFile edition easier.

+ Edit from the source editor

	The source editor has been enhanced to help you differentiate your data from the different column using a color mechanism

+ Edit from a table view representing the csv data

	The table editing is definitely the added value of the plugin and allows a bunch of operation on your csv file among the following features

	- Easy editing of data
	- Insertion and deletion of table rows
	- Insertion and deletion of table column
	- Filtering of data
	- Customize view with hidding/display of column to simplify the view
	- Manage your column with moving/resizing columns
	- Sorting data in column
	- Manage your own CSV settings
	
![](images/Editor_Table_Tab.png)
![](images/Editor_Source_Tab.png)
![](images/Preferences.png)	

## History

Optional maven/tycho build was added in 2017 by Paul Verest.

### New features of 1.2.x

In the new CSVEdit version, it is now possible to edit a row in a form.

![Detailed edition](http://www.mathieusavy.com/images/github/CSVEdit-detailed.png "Detailed edition")

And even specify columns containing multiple values and their delimiter.

To do so, go in preferences/CSV Editor Preferences:

+ in `header regex` put a regex matching a part of all column headers you want to display as a table. For instance, the picture below uses the regex (customer|order_id).
+ in `delimiter to use as value delimiter`, put the delimiter separating each value in your cell.

![List with multivalues](http://www.mathieusavy.com/images/github/CSVEdit-multivalues.png "List with multivalues")

## Code overview

![](images/code_overview.png)

<a href="http://with-eclipse.github.io/" target="_blank">
<img alt="with-Eclipse logo" src="http://with-eclipse.github.io/with-eclipse-0.jpg" />
</a>

