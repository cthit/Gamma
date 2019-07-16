package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

var userQuery string = "insert into ituser (id, cid, password, nick, first_name, last_name, email, phone, language, avatar_url, gdpr, user_agreement, acceptance_year) \nvalues "

var userMap = make(map[string]string)

type ITUser struct {
	id             string
	cid            string
	password       string
	nick           string
	firstName      string
	lastName       string
	email          string
	phone          string
	language       string
	avatarUrl      string
	gpdr           string
	useragreement  string
	acceptanceYear string
}

func WriteUser(user ITUser, file *os.File) {
	file.WriteString("('" + user.id + "', ")
	file.WriteString("'" + user.cid + "', ")
	file.WriteString("'" + user.password + "', ")
	file.WriteString("'" + user.nick + "', ")
	file.WriteString("'" + user.firstName + "', ")
	file.WriteString("'" + strings.Replace(user.lastName, "'", "''", -1) + "', ")
	file.WriteString("'" + user.email + "', ")
	file.WriteString("'" + user.phone + "', ")
	file.WriteString("'" + user.language + "', ")
	file.WriteString("'" + user.avatarUrl + "', ")
	file.WriteString(user.gpdr + ", ")
	file.WriteString(user.useragreement + ", ")
	file.WriteString(user.acceptanceYear + ")")
	userMap[user.cid] = user.id
}
func ParseUsers(file *os.File, record [][]string) {
	fmt.Println("Parsing Users to file ")
	file.WriteString(userQuery)
	for j := 2; j < len(record); j++ {
		line := record[j]
		language := line[15]
		if language == "en" {
			language = "en"
		} else {
			language = "sv"
		}
		gdpr := line[8]
		if gdpr != "TRUE" {
			gdpr = "FALSE"
		}
		year, _ := strconv.Atoi(line[4])
		if year < 2001 {
			year = 2001
		}
		cid := line[18]
		if len(cid) > 10 {
			cid = cid[:10]
		}
		WriteUser(ITUser{
			id:             GenerateUUID(),
			cid:            cid,
			password:       line[20],
			nick:           line[14],
			firstName:      line[10],
			lastName:       line[16],
			email:          line[13],
			phone:          line[17],
			language:       language,
			avatarUrl:      line[5],
			gpdr:           gdpr,
			useragreement:  line[3],
			acceptanceYear: strconv.Itoa(year),
		}, file)
		if j == len(record)-1 {
			file.WriteString(";")
		} else {
			file.WriteString(",\n")
		}
	}
}
