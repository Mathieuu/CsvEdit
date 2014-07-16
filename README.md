CsvEdit
=======

CSV Editor plugin for Eclipse

## What is this ?

### Warning
This repository is a fork of [https://code.google.com/p/csvedit/](https://code.google.com/p/csvedit/). It contains the latest functionnalities available for CSVEdit but most of the new features are still being tested. If you look for a more reliable version of the plugin you should probably visit the original repository. 

- The current stable release available on Eclipse Marketplace is 1.1.3.
- The version available in this repository is 1.2.x

As soon as a relevant feature will be mature enough, it will be added in the original repository.

###CSVEdit

CSVEdit make CSVFile edition easier.

+ Edit from the source editor

	The source editor has been enhanced to help you differentiate your data from the different column using a color mechanism

+ Edit from a table view representing the csv data

	The table editing is definitly the added value of the plugin and allows a bunch of operation on your csv file among the following features

	- Easy editing of data
	- Insertion and deletion of table rows
	- Insertion and deletion of table column
	- Filtering of data
	- Customize view with hidding/display of column to simplify the view
	- Manage your column with moving/resizing columns
	- Sorting data in column
	- Manage your own CSV settings

## New features of 1.2.x

In the new CSVEdit version, it is now possible to edit a row in a form.

![Detailed edition](http://mathieusavy.com/images/github/CSVEdit-detailed.png "Detailed edition")

And even specify columns containing multiple values and their delimiter.

To do so, go in preferences/CSV Editor Preferences:

+ in `header regex` put a regex matching a part of all column headers you want to display as a table. For instance, the picture below uses the regex (customer|order_id).
+ in `delimiter to use as value delimiter`, put the delimiter separating each value in your cell.

![List with multivalues](http://mathieusavy.com/images/github/CSVEdit-multivalues.png "List with multivalues")

## How to get the latest version

The latest version (1.2) is available with update-site. This is definitly the easiest and straightforward way to install the plugin.

In Eclipse, help, install new software, add the following URL and follow the instructions.

[https://raw.githubusercontent.com/SegFaultError/CsvEdit/master/csvedit.update/](https://raw.githubusercontent.com/SegFaultError/CsvEdit/master/csvedit.update/)
