## PDF Fixer

The plan is for this to evolve into a simple commandline tool for manipulating PDF files. Currently it's just some experiments.

A rough plan:

* Read config from xml/json config files
* (A simple UI for making configs)
* ("Draft"/dryrun mode)
* Modular design, be able to…
  * Remove all items from specified layer, from all pages
  * Delete specific pages
  * Insert specific pages from one PDF into another, at specified locations (including first/last)
  * Insert blank pages, at specific locations (first/last)
  * Set title of PDF (Maybe other metadata)
