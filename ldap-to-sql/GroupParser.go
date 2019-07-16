package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

var superGroupQuery = "\n insert into fkit_super_group (id, name, pretty_name, type) \nvalues "
var postAddQuery = "\n insert into post (id, post_name) \nvalues "
var membershipQuery = "\n insert into membership (ituser_id, fkit_group_id, post_id, unofficial_post_name) \nvalues"
var membershipQueryNoUser = "\n insert into no_account_membership (user_name, fkit_group_id, post_id, unofficial_post_name) \nvalues"
var groupQuery = "\n insert into fkit_group (id, name, pretty_name, description, function, email, becomes_active, becomes_inactive) \nvalues"
var TextQuery = "\n insert into internal_text (id, sv, en) \nvalues"
var groupToSuperGroupQuery = "\n insert into fkit_group_to_super_group (fkit_super_group_id, fkit_group_id) \nvalues"

var superGroupMap = make(map[string]string)
var postMap = make(map[string]string)
var groupMap = make(map[string]string)
var isSpecialMember = make(map[string]bool)

type SuperGroup struct {
	id          string
	name        string
	pretty_name string
	grouptype   string
}

type Group struct {
	id               string
	name             string
	pretty_name      string
	description      string
	function         string
	email            string
	becomes_active   string
	becomes_inactive string
}

type Membership struct {
	ituser_id           string
	fkit_group_id       string
	post_id             string
	unoficial_post_name string
}

type group_to_super_group struct {
	super_group string
	group       string
}

type Text struct {
	id string
	sv string
	en string
}

func ParseSuperGroups(file *os.File, data [][]string) {
	fmt.Println("parsing super groups to file")
	AddDefaultPosts(file)
	for i := 2; i < len(data); i++ {
		if strings.Contains(data[i][0], "cn=ordf") || strings.Contains(data[i][0], "cn=kassor") {
			isSpecialMember[ExtractMember(data[i][7])+ExtractGroupNameUser(data[i][0])] = true
		}
	}
	for i := 2; i < len(data); i++ {
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
				grouptype:   data[i][8],
			}, file)
		} else if strings.Contains(data[i][1], "itGroup") {
			superGroupq1 := strings.SplitAfter(data[i][0], ",ou=")
			superGroupq2 := strings.Split(superGroupq1[1], ",ou=")
			name := ExtractGroupName(data[i][0])
			supergroup := superGroupq2[0]
			becomes_inactiveq1 := data[i][3]
			descriptionUUID := GenerateUUID()
			WriteInternalText(Text{
				id: descriptionUUID,
				sv: "",
				en: "",
			}, file)
			functionUUID := GenerateUUID()
			WriteInternalText(Text{
				id: functionUUID,
				sv: data[i][4],
				en: data[i][4],
			}, file)
			l := len(becomes_inactiveq1)
			if becomes_inactiveq1[l-2] > 57 || becomes_inactiveq1[l-1] > 57 {
				continue
			}
			bi1 := int(becomes_inactiveq1[l-2] - 48)
			bi2 := int(becomes_inactiveq1[l-1] - 48)
			bi := bi1*10 + bi2
			year := strconv.Itoa(2000 + bi)
			lastYear := strconv.Itoa(2000 + bi - 1)
			WriteGroup(Group{
				id:               GenerateUUID(),
				name:             name,
				pretty_name:      data[i][5],
				description:      descriptionUUID,
				function:         functionUUID,
				email:            data[i][11],
				becomes_active:   "'" + lastYear + "1231'",
				becomes_inactive: "'" + year + "1231'",
			}, file)
			writeGroupToSuperGroup(group_to_super_group{
				super_group: superGroupMap[supergroup],
				group:       groupMap[name],
			}, file)
			if strings.Contains(data[i][7], "uid") {
				mems := strings.Split(data[i][7], "uid=")
				postNameq1 := strings.Split(data[i][10], "|")
				nPostNames := len(postNameq1)
				if nPostNames == 1 {
					nPostNames = 0
				}
				i := 0
				for j := 0; j < len(mems)-1; j++ {
					mems1 := strings.Split(mems[j], ",")
					nm := strings.Replace(name, "1", "", -1)
					nm = strings.Replace(nm, "8", "", -1)
					nm = strings.Replace(nm, "9", "", -1)
					postname := "medlem"
					userName := mems1[0]
					fmt.Println(nPostNames)
					fmt.Println(i)
					if i < nPostNames {
						fmt.Println(postNameq1[j])
						postNameq2 := strings.Split(postNameq1[j], ";")
						if isSpecialMember[strings.Replace(postNameq2[1], " ", "", -1)+nm] {
							i++
							continue
						}
						postname = postNameq2[0]
						userName = strings.Replace(postNameq2[1], " ", "", -1)
						i++
					}

					if userMap[userName] != "" {
						WriteMembership(Membership{
							ituser_id:           userMap[userName],
							fkit_group_id:       groupMap[name],
							post_id:             postMap["member"],
							unoficial_post_name: postname,
						}, file)
					} else {
						WriteMembershipNoUser(Membership{
							ituser_id:           userName,
							fkit_group_id:       groupMap[name],
							post_id:             postMap["member"],
							unoficial_post_name: postname,
						}, file)
					}
				}
			}
		}
	}
	for i := 2; i < len(data); i++ {

		if strings.Contains(data[i][0], "cn=ordf") {
			WriteMembership(Membership{
				ituser_id:           userMap[ExtractMember(data[i][7])],
				fkit_group_id:       groupMap[ExtractGroupNameFromPost(data[i][0])+getYear(ExtractGroupNameFromPost(data[i][0]))],
				post_id:             postMap["chairman"],
				unoficial_post_name: data[i][5],
			}, file)
		} else if strings.Contains(data[i][0], "cn=kassor") {
			WriteMembership(Membership{
				ituser_id:           userMap[ExtractMember(data[i][7])],
				fkit_group_id:       groupMap[ExtractGroupNameFromPost(data[i][0])+getYear(ExtractGroupNameFromPost(data[i][0]))],
				post_id:             postMap["treasurer"],
				unoficial_post_name: data[i][5],
			}, file)
		}
	}
}

func WriteMembership(membership Membership, file *os.File) {
	file.WriteString(membershipQuery)
	file.WriteString("('" + membership.ituser_id + "', ")
	file.WriteString("'" + membership.fkit_group_id + "', ")
	file.WriteString("'" + membership.post_id + "', ")
	file.WriteString("'" + membership.unoficial_post_name + "'); ")
}

func WriteMembershipNoUser(membership Membership, file *os.File) {
	file.WriteString(membershipQueryNoUser)
	file.WriteString("('" + membership.ituser_id + "', ")
	file.WriteString("'" + membership.fkit_group_id + "', ")
	file.WriteString("'" + membership.post_id + "', ")
	file.WriteString("'" + membership.unoficial_post_name + "'); ")
}

func WriteInternalText(text Text, file *os.File) {
	file.WriteString(TextQuery)
	file.WriteString("('" + text.id + "', ")
	file.WriteString("'" + text.sv + "', ")
	file.WriteString("'" + text.en + "'); ")
}

func ExtractMember(memberLine string) string {
	memberq := strings.SplitAfter(memberLine, "uid=")
	memberq = strings.Split(memberq[1], ",")
	member := memberq[0]
	return member
}

func ExtractGroupName(data string) string {
	nameq1 := strings.SplitAfter(data, "cn=")
	nameq2 := strings.Split(nameq1[1], ",ou")
	return nameq2[0]
}

func ExtractGroupNameUser(data string) string {
	nameq1 := strings.SplitAfter(data, "cn=")
	nameq2 := strings.Split(nameq1[2], ",ou")
	return nameq2[0]
}

func ExtractGroupNameFromPost(data string) string {
	nameq1 := strings.SplitAfter(data, "cn=")
	nameq2 := strings.Split(nameq1[2], ",ou")
	return nameq2[0]
}

func AddDefaultPosts(file *os.File) {
	textUUID1 := GenerateUUID()
	textUUID2 := GenerateUUID()
	textUUID3 := GenerateUUID()
	chairman := "chairman"
	treasurer := "treasurer"
	member := "member"
	WriteInternalText(Text{
		id: textUUID1,
		sv: "ordförande",
		en: chairman,
	}, file)
	WriteInternalText(Text{
		id: textUUID2,
		sv: "kassör",
		en: treasurer,
	}, file)
	WriteInternalText(Text{
		id: textUUID3,
		sv: "medlem",
		en: member,
	}, file)
	file.WriteString(postAddQuery)
	uuid1 := GenerateUUID()
	uuid2 := GenerateUUID()
	uuid3 := GenerateUUID()

	file.WriteString("('" + uuid1 + "', ")
	file.WriteString("'" + textUUID1 + "'), \n")
	file.WriteString("('" + uuid2 + "', ")
	file.WriteString("'" + textUUID2 + "'), \n")
	file.WriteString("('" + uuid3 + "', ")
	file.WriteString("'" + textUUID3 + "'); \n")
	postMap[chairman] = uuid1
	postMap[treasurer] = uuid2
	postMap[member] = uuid3
}

func getYear(group string) string {
	if group == "8bit" {
		return "17"
	}
	if group == "prit" || group == "sexit" {
		return "19"
	}
	return "18"
}

func WriteSuperGroup(group SuperGroup, file *os.File) {
	file.WriteString(superGroupQuery)
	file.WriteString("('" + group.id + "', ")
	file.WriteString("'" + group.name + "', ")
	file.WriteString("'" + group.pretty_name + "', ")
	file.WriteString("'" + group.grouptype + "'); ")
	superGroupMap[group.name] = group.id
}

func WriteGroup(group Group, file *os.File) {
	file.WriteString(groupQuery)
	file.WriteString("('" + group.id + "', ")
	file.WriteString("'" + group.name + "', ")
	file.WriteString("'" + group.pretty_name + "', ")
	file.WriteString("'" + group.description + "', ")
	file.WriteString("'" + group.function + "', ")
	file.WriteString("'" + group.email + "', ")
	file.WriteString(group.becomes_active + ", ")
	file.WriteString(group.becomes_inactive + ", ")
	groupMap[group.name] = group.id
}

func writeGroupToSuperGroup(group group_to_super_group, file *os.File) {
	file.WriteString(groupToSuperGroupQuery)
	file.WriteString("('" + group.super_group + "', ")
	file.WriteString("'" + group.group + "');")
}
