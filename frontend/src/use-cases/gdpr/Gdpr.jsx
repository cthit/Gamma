import {
    DigitTable,
    DigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./Gdpr.translations.json";
import {
    CID,
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../api/users/props.users.api";
import * as _ from "lodash";

class Gdpr extends React.Component {
    constructor(props) {
        super();
        this.state = {
            lastSelected: []
        };

        props.getUsersWithGDPR().then(response => {
            this.setState({
                lastSelected: response.data
                    .filter(user => user.gdpr)
                    .map(user => user.id)
            });
            props.gammaLoadingFinished();
        });
    }

    _generateHeaderTexts = text => {
        const output = {};

        output[CID] = text.Cid;
        output[FIRST_NAME] = text.FirstName;
        output[LAST_NAME] = text.LastName;
        output[NICK] = text.Nick;
        output["__checkbox"] = text.HasGDPR;

        return output;
    };

    render() {
        const { lastSelected } = this.state;
        const { users, setGDPRValue, getUsersWithGDPR } = this.props;

        return (
            <DigitLayout.Center>
                <DigitTranslations
                    translations={translations}
                    render={text => (
                        <DigitTable
                            search
                            showSearchableProps
                            titleText={text.Users}
                            searchText={text.SearchForUsers}
                            idProp={ID}
                            startOrderBy={FIRST_NAME}
                            onSelectedUpdated={selected => {
                                const c = _.xorWith(
                                    selected,
                                    lastSelected,
                                    _.isEqual
                                );

                                if (c.length > 0) {
                                    var newGDPRValue = false;

                                    if (selected.length > lastSelected.length) {
                                        //add
                                        newGDPRValue = true;
                                    }

                                    setGDPRValue(c[0], {
                                        gdpr: newGDPRValue
                                    }).then(() =>
                                        getUsersWithGDPR().then(response => {
                                            this.setState({
                                                lastSelected: response.data
                                                    .filter(user => user.gdpr)
                                                    .map(user => user.id)
                                            });
                                        })
                                    );
                                }
                            }}
                            selected={users
                                .filter(user => user.gdpr)
                                .map(user => user.id)}
                            columnsOrder={[CID, FIRST_NAME, NICK, LAST_NAME]}
                            headerTexts={this._generateHeaderTexts(text)}
                            data={users.map(user => {
                                return {
                                    ...user,
                                    __checkbox: user.gdpr
                                };
                            })}
                        />
                    )}
                />
            </DigitLayout.Center>
        );
    }
}

export default Gdpr;
