package main

import (
	"fmt"
	"os"
	"strings"
)

var superGroupQuery = "\n insert into fkit_super_group (id, name, pretty_name, type) \nvalues "
var postAddQuery = "\n insert into post (id, post_name) \nvalues "
var membershipQuery = "\n insert into membership (it_user_id, fkit_group_id, post_id, unoficial_post_name) \nvalues"
var groupQuery = "\n insert into fkit_group (id, name, super_group, pretty_name, description, function, email, becomes_active, becomes_inactive, internal_year) \nvalues"

var superGroupMap = make(map[string]string)
var postMap = make(map[string]string)
var groupMap = make(map[string]string)

type SuperGroup struct {
	id string
	name string
	pretty_name string
	grouptype string
}

type Group struct {
	 id string
	 name string
	 super_group string
	 pretty_name string
	 description string
	 function string
	 email string
	 becomes_active string
	 becomes_inactive string
	 internal_year string
}

type Membership struct {
	ituser_id string
	fkit_group_id string
	post_id string
	unoficial_post_name string
}

func ParseSuperGroups(file *os.File, data [][]string) {
	fmt.Println("parsing super groups to file")
	AddDefaultPosts(file)
	for i := 2 ; i < len(data) ; i++ {
		if strings.Contains(data[i][1], "organizationalUnit") {
			prettyName := data[i][5]
			if prettyName == "" {
				prettyName = data[i][2]
			}
			i++
			WriteSuperGroup(SuperGroup{
				id:          GenerateUUID(),
				name:        data[i-1][2],
				pretty_name: prettyName,
				grouptype:   data[i][9],
			}, file)
			file.WriteString(";")
		} else if strings.Contains(data[i][1], "itGroup"){
			superGroupq1 := strings.SplitAfter(data[i][0], ",ou=")
			fmt.Println(superGroupq1)
			superGroupq2 := strings.Split(superGroupq1[1], ",ou=")
			fmt.Println(superGroupq2)
			supergroup := superGroupq2[0]
			fmt.Println(supergroup)
			becomes_inactiveq1 := data[i][3]
			l := len(becomes_inactiveq1)
			WriteGroup(Group{
				id:					GenerateUUID(),
				name: 				data[i][5],
				super_group: 		superGroupMap[supergroup],
				pretty_name:    	data[i][5],
				description: 		"",
				function:       	data[i][4],
				email: 				data[i][7],
				becomes_active: 	"20" + string(becomes_inactiveq1[l-2]) + string(becomes_inactiveq1[l-1]-1) + "-12-31",
				becomes_inactive:	"20" + string(becomes_inactiveq1[l-2]) + string(becomes_inactiveq1[l-1]) + "-12-31",
				internal_year:  	"20" + string(becomes_inactiveq1[l-2]) + string(becomes_inactiveq1[l-1]),
			}, file)
			if strings.Contains(data[i][8], "uid") {
				WriteMembership(Membership{
					ituser_id: userMap[extractMember(data[i][8])],
					//fkit_group_id:
					post_id:             postMap["member"],
					unoficial_post_name: data[i][5],
				}, file)
			}
		} else if strings.Contains(data[i][0], "cn=ordf") {
			WriteMembership(Membership{
				ituser_id:				userMap[extractMember(data[i][8])],
			//	fkit_group_id:
				post_id:				postMap["chairman"],
				unoficial_post_name:	data[i][5],
			}, file)
		} else if strings.Contains(data[i][0], "cn=kassor") {
			WriteMembership(Membership{
				ituser_id:				userMap[extractMember(data[i][8])],
				//fkit_group_id:
				post_id:				postMap["treasurer"],
				unoficial_post_name: 	data[i][5],
			}, file)
		}

	}
}

func WriteMembership(membership Membership, file *os.File) {
	file.WriteString(membershipQuery)
	file.WriteString("('" + membership.ituser_id + "', ")
	file.WriteString("'" +  membership.fkit_group_id + "', ")
	file.WriteString("'" + membership.post_id + "', ")
	file.WriteString("'" + membership.unoficial_post_name + "') ")
}

func extractMember(memberLine string) string{
	memberq := strings.SplitAfter(memberLine, "uid=")
	memberq = strings.Split(memberq[1], ",")
	member := memberq[0]
	return member
}

func AddDefaultPosts(file *os.File) {
	file.WriteString(postAddQuery)
	uuid1 := GenerateUUID()
	uuid2 := GenerateUUID()
	uuid3 := GenerateUUID()
	chairman := "chairman"
	treasurer := "treasurer"
	member := "member"
	file.WriteString("('" + uuid1 + "', ")
	file.WriteString("'" + chairman + "'), \n")
	file.WriteString("('" + GenerateUUID() + "', ")
	file.WriteString("'" + treasurer + "'), \n")
	file.WriteString("('" + GenerateUUID() + "', ")
	file.WriteString("'" + member + "'), \n")
	postMap[chairman] = uuid1
	postMap[treasurer] = uuid2
	postMap[member] = uuid3
}

func WriteSuperGroup(group SuperGroup, file *os.File) {
	file.WriteString(superGroupQuery)
	file.WriteString("('" + group.id + "', ")
	file.WriteString("'" + group.name + "', ")
	file.WriteString("'" + group.pretty_name + "', ")
	file.WriteString("'" + group.grouptype + "') ")
	superGroupMap[group.name] = group.id
}

func WriteGroup(group Group, file *os.File) {
	file.WriteString(groupQuery)
	file.WriteString("('" + group.id + "',, ")
	file.WriteString("'" + group.name + "', ")
	file.WriteString("'" + group.super_group + "', ")
	file.WriteString("'" + group.pretty_name + "', ")
	file.WriteString("'" + group.description + "', ")
	file.WriteString("'" + group.function + "', ")
	file.WriteString("'" + group.email + "', ")
	file.WriteString( group.becomes_active + ", ")
	file.WriteString( group.becomes_inactive + ", ")
	file.WriteString("'" + group.internal_year + "') ")
}
