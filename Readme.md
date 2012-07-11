confluence-macro-exchange-contacts
==================================
This Atlassian Confluence marcro allows you to view all contacts of a Exchange public folder inside your wiki page.

Just write

	{contacts|folder="my-folder\customerA\projekt-contactsA"}

as markup and that it is.  

Internals
---------
The macro uses ActiveObjects to synchronize the remote contacts with the local database.
Communication between Exchange Server and Atlassian will be established through EWS API. You must use Exchange 2007 or later and enable EWS inside your IIS.

Contact
-------
schakkonator [AT] googlemail [DOT] com / http://twitter.com/schakko

License
-------
Copyright (C) 2012  Christopher Klein

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
