# Gamma Mocking
Using mock.json, or providing your .json file is an easy way to create users, groups, posts, and supergroups for usage when creating mock data in your application that uses gamma. By always providing the same ID:s, you can easily, for example, create a booking with a given user id that will be the same for everyone who tries to develop on your application and tries to use your mock data. 

Note that this file will only run if there's no admin user, i.e. the database is empty. 

The json document represents an object of `MockData.java`. `DbInitalizer.java` has the logic to actually insert the mock data into the database. But here's a quick overview of the different props that can be used:

## `groups`

Creates `FKITGroup`:s. Each object in the array is represented in code by `MockFKITGroup.java`. The available props are: 

* `id`: UUID
* `name`: String
* `prettyName`: String
* `active`: boolean
* `members`: Array
* `function`: Object | null
* `description`: Object | null

###`active`

If `active` is false, then the `becomesActive` date will be a year ago, and `becomesInactive` will be yesterday. If `active` is true, then `becomesActive` will be yesterday and `becomesInactive` will be a year from now.

### `members`

An array of `MockMembership.java`. Props are:

* `userId`: UUID
* `postId`: UUID
* `unofficialPostName`: String | null

### `function` and `description`

Are `Text.java` objects which means they take in an object that has the properties `sv` and `en`. The value for them are strings. 

### Other notes

`email` will be `name` + @chalmers.it. 

##`users`

Creates `ITUser.java`. Each object in the array is represented in code by `MockITUser.java`. The available props are:

* `id`: UUID
* `cid`: String
* `nick`: String
* `firstName`: String
* `lastName`: String
* `acceptanceYear`: Year

`acceptanceYear` can be a value between 2001 - current year.

## `posts`

Create `Post.java`. Each object in the array is represented in code by `MockPost.java`. The available props are:

* `id`: UUID
* `postName`: Object

### `postName`

As with `function` and `description` in groups, `postName` is represented by the `Text.java` class. 

## `superGroups`

Creates `FKITSuperGroup.java`. Each object in the array is represented in code by `MockFKITSuperGroup.java`. The available props are:

* `id`: UUID
* `name`: String
* `prettyName`: String
* `type`: GroupType
* `groups`: Array

### `type`

An enum where the possible values are: `ADMIN`, `SOCIETY`, `COMMITTEE`, `BOARD`, `ALUMNI` and `FUNCTIONARIES`.

### `groups`

Only an array of UUID that represents a `FKITGroup`. 