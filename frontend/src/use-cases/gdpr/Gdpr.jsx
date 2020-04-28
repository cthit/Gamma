import {
    DigitSelectMultipleTable,
    useDigitTranslations,
    useDigitToast
} from "@cthit/react-digit-components";
import React, { useCallback, useEffect, useState } from "react";
import translations from "./Gdpr.translations.json";
import {
    CID,
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../api/users/props.users.api";
import * as _ from "lodash";
import InsufficientAccess from "../../common/views/insufficient-access";
import { getUsersWithGDPRMinified } from "../../api/gdpr/get.gdpr.api";
import { setGDPRValue } from "../../api/gdpr/put.gdpr.api";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import useGammaHasAuthority from "../../common/hooks/use-gamma-has-authority/use-gamma-has-authority";

function _generateHeaderTexts(text) {
    const output = {};

    output[CID] = text.Cid;
    output[FIRST_NAME] = text.FirstName;
    output[LAST_NAME] = text.LastName;
    output[NICK] = text.Nick;
    output[ID] = text.Id;
    output["__checkbox"] = text.HasGDPR;

    return output;
}

const Gdpr = () => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const [users, setUsers] = useState(null);

    const [lastSelected, setLastSelected] = useState([]);
    const admin = useGammaIsAdmin();
    const gdpr = useGammaHasAuthority("gdpr");
    const access = admin || gdpr;
    const getUsersWithGDPRCallback = useCallback(getUsersWithGDPRMinified, []);

    useEffect(() => {
        if (access) {
            getUsersWithGDPRCallback().then(response => {
                setUsers(response.data);
                setLastSelected(
                    response.data.filter(user => user.gdpr).map(user => user.id)
                );
            });
        }
    }, [access, getUsersWithGDPRCallback]);

    if (!access) {
        return <InsufficientAccess />;
    }

    if (users == null) {
        return null;
    }

    return (
        <DigitSelectMultipleTable
            disableSelectAll
            search
            titleText={text.Users}
            searchText={text.SearchForUsers}
            idProp={ID}
            startOrderBy={FIRST_NAME}
            onChange={selected => {
                const c = _.xorWith(selected, lastSelected, _.isEqual);

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
                            queueToast({
                                text:
                                    text.SuccessfullySetOfGDPRTo +
                                    " " +
                                    _.find(users, {
                                        id: c[0]
                                    })[NICK] +
                                    " " +
                                    text.To +
                                    ": " +
                                    newGDPRValue
                            });

                            getUsersWithGDPRCallback().then(response => {
                                setUsers(response.data);
                                setLastSelected(
                                    response.data
                                        .filter(user => user.gdpr)
                                        .map(user => user.id)
                                );
                            });
                        })
                        .catch(() => {
                            queueToast({
                                text: text.SomethingWentWrong
                            });
                        });
                }
            }}
            value={users.filter(user => user.gdpr).map(user => user.id)}
            columnsOrder={[CID, FIRST_NAME, NICK, LAST_NAME]}
            headerTexts={_generateHeaderTexts(text)}
            data={users.map(user => {
                return {
                    ...user,
                    __checkbox: user.gdpr
                };
            })}
        />
    );
};

export default Gdpr;
