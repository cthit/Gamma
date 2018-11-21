import { DigitTable, DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowAllUsers.screen.translations.json";

const ShowAllUsers = ({ users }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Users.Screen.ShowAllUsers"
        render={text => (
            <div>
                {console.log(users)}

                <DigitTable
                    titleText={text.Users}
                    searchText={text.SearchForUsers}
                    idProp="cid"
                    startOrderBy="firstName"
                    columnsOrder={[
                        "first_name",
                        "nickname",
                        "last_name",
                        "cid",
                        "acceptance_year"
                    ]}
                    headerTexts={{
                        firstName: text.FirstName,
                        lastName: text.LastName,
                        cid: text.Cid,
                        nick: text.Nick,
                        acceptanceYear: text.AcceptanceYear,
                        __link: text.Details
                    }}
                    data={users.map(user => {
                        return { ...user, __link: "/users/" + user.cid };
                    })}
                    emptyTableText={text.NoUsers}
                />
            </div>
        )}
    />
);

export default ShowAllUsers;
