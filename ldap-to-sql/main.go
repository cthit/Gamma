package main

import (
	"fmt"
	"os"
	"encoding/csv"
	"os/exec"
	"strings"
)


func main() {
	fmt.Println("Started to transfer LDAP to SQL")
	user := ImportCSV("full_user.csv")
	groups := ImportCSV("groups.csv")
	err := os.Remove("insertions.sql")
	file, err := os.Create("insertions.sql")
	if err != nil {
		panic(err)
	}
	defer file.Close()
	ParseUsers(file, user)
	ParseSuperGroups(file, groups)

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
	if err != nil {
		fmt.Println(err)
	}
	id := strings.Replace(string (uuid), "\n", "", -1)
	return id
}
