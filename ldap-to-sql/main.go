package main

import (
	"fmt"
	"os"
	"encoding/csv"
	"os/exec"
	"strings"
)
var query string = "insert into ituser (id, cid, password, nick, first_name, last_name, email, phone, language, avatar_url, gdpr, user_agreement, acceptance_year) \nvalues "
type ITUser struct {
	id string
	cid string
	password string
	nick string
	firstName string
	lastName string
	email string
	phone string
	language string
	avatarUrl string
	gpdr string
	useragreement string
	acceptanceYear string
}

func main() {
	fmt.Println("Started to transfer LDAP to SQL")
	record := ImportCSV("full_user.csv")
	err := os.Remove("insertions.sql")
	println(err)
	file, err := os.Create("insertions.sql")
	if err != nil {
		panic(err)
	}
	defer file.Close()
	file.WriteString(query)
	for j := 2; j < len(record); j++ {
		line := record[j]
		language := line[15]
		if language == "en" {
			language = "ENGLISH"
		} else {
			language = "SWEDISH"
		}
		gdpr := line[8]
		if gdpr != "TRUE" {
			gdpr = "FALSE"
		}
		WriteUser(ITUser{
			id: strings.Replace(GenerateUUID(), "\n", "", -1),
			cid:line[18],
			password:line[20],
			nick:line[14],
			firstName:line[10],
			lastName:line[16],
			email:line[13],
			phone:line[17],
			language: language,
			avatarUrl:line[5],
			gpdr:gdpr,
			useragreement:line[3],
			acceptanceYear:line[4],
		}, *file)
		if j == len(record) - 1 {
			file.WriteString(";")
		} else {
			file.WriteString(",\n")
		}
	}
}

func WriteUser(user ITUser, file os.File){
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
}

func ImportCSV(fileName string) [][]string{
	file, err := os.Open(fileName)
	if err != nil {
		fmt.Println("error ", err)
	}
	defer file.Close()
	reader := csv.NewReader(file)
	record, err := reader.ReadAll()
	if err != nil {
		fmt.Println("Error", err)
	}
	return record
}

func GenerateUUID() string {
	uuid, err := exec.Command("uuidgen").Output()
	if err == nil {
		fmt.Println(err)
	}
	return string (uuid)
}
