import {
    DigitTable,
    DigitTranslations,
    DigitFAB,
    DigitLayout,
    DigitDesign,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import translations from "./ShowAllUsers.screen.translations.json";
import { Add } from "@material-ui/icons";
import {
    ACCEPTANCE_YEAR,
    CID,
    FIRST_NAME,
    LAST_NAME,
    NICKNAME
} from "../../../../api/users/props.users.api";

class ShowAllUsers extends Component {
    componentDidMount() {
        const { getUsersMinified, gammaLoadingFinished } = this.props;

        getUsersMinified().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    static generateHeaderTexts(text) {
        const headerTexts = {};
        headerTexts[FIRST_NAME] = text.FirstName;
        headerTexts[LAST_NAME] = text.LastName;
        headerTexts[CID] = text.Cid;
        headerTexts[NICKNAME] = text.Nick;
        headerTexts[ACCEPTANCE_YEAR] = text.AcceptanceYear;
        headerTexts["__link"] = text.Details;
        return headerTexts;
    }

    render() {
        const { users } = this.props;
        return (
            <DigitIfElseRendering
                test={users != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Users.Screen.ShowAllUsers"
                        render={text => (
                            <div>
                                <DigitTable
                                    titleText={text.Users}
                                    searchText={text.SearchForUsers}
                                    idProp={CID}
                                    startOrderBy={FIRST_NAME}
                                    columnsOrder={[
                                        FIRST_NAME,
                                        NICKNAME,
                                        LAST_NAME,
                                        CID,
                                        ACCEPTANCE_YEAR
                                    ]}
                                    headerTexts={ShowAllUsers.generateHeaderTexts(
                                        text
                                    )}
                                    data={users.map(user => {
                                        return {
                                            ...user,
                                            __link: "/users/" + user.id
                                        };
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
                )}
            />
        );
    }
}

export default ShowAllUsers;
