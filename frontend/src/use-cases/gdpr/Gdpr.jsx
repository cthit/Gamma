import {
    DigitTable,
    DigitTranslations,
    DigitLayout,
    DigitToastActions
} from "@cthit/react-digit-components";
import React, { useEffect, useState } from "react";
import translations from "./Gdpr.translations.json";
import {
    CID,
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../api/users/props.users.api";
import * as _ from "lodash";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";
import { useDispatch } from "react-redux";

function _generateHeaderTexts(text) {
    const output = {};

    output[CID] = text.Cid;
    output[FIRST_NAME] = text.FirstName;
    output[LAST_NAME] = text.LastName;
    output[NICK] = text.Nick;
    output["__checkbox"] = text.HasGDPR;

    return output;
}

const Gdpr = ({
    users,
    setGDPRValue,
    getUsersWithGDPR,
    deltaLoadingFinished
}) => {
    const dispatch = useDispatch();
    const [lastSelected, setLastSelected] = useState([]);
    const admin = useIsAdmin();

    useEffect(() => {
        if (admin) {
            getUsersWithGDPR().then(response => {
                console.log(response);
                setLastSelected(
                    response.data.filter(user => user.gdpr).map(user => user.id)
                );
                deltaLoadingFinished();
            });
        }
    }, [admin]);

    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitLayout.Center>
            <DigitTranslations
                translations={translations}
                render={text => (
                    <DigitTable
                        search
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
                                })
                                    .then(() => {
                                        dispatch(
                                            DigitToastActions.digitToastOpen({
                                                text:
                                                    text.SuccessfullySetOfGDPRTo +
                                                    " " +
                                                    c[0] +
                                                    " " +
                                                    text.To +
                                                    ": " +
                                                    newGDPRValue
                                            })
                                        );

                                        getUsersWithGDPR().then(response => {
                                            setLastSelected(
                                                response.data
                                                    .filter(user => user.gdpr)
                                                    .map(user => user.id)
                                            );
                                        });
                                    })
                                    .catch(() => {
                                        dispatch(
                                            DigitToastActions.digitToastOpen({
                                                text: text.SomethingWentWrong
                                            })
                                        );
                                    });
                            }
                        }}
                        selected={users
                            .filter(user => user.gdpr)
                            .map(user => user.id)}
                        columnsOrder={[CID, FIRST_NAME, NICK, LAST_NAME]}
                        headerTexts={_generateHeaderTexts(text)}
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
};

export default Gdpr;
