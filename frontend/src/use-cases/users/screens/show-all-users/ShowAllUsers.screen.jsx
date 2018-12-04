import {
    DigitTable,
    DigitTranslations,
    DigitFAB,
    DigitLayout,
    DigitDesign
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowAllUsers.screen.translations.json";
import { Add } from "@material-ui/icons";

const ShowAllUsers = ({ users }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Users.Screen.ShowAllUsers"
        render={text => (
            <div>
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
                <DigitLayout.DownRightPosition>
                    <DigitDesign.Link to="/users/add">
                        <DigitFAB icon={Add} secondary />
                    </DigitDesign.Link>
                </DigitLayout.DownRightPosition>
            </div>
        )}
    />
);

export default ShowAllUsers;
