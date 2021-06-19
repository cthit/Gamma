import * as _ from "lodash";
import React, { useCallback, useEffect, useState } from "react";

import {
    DigitSelectMultipleTable,
    useDigitTranslations,
    useDigitToast,
    DigitLoading,
    DigitLayout
} from "@cthit/react-digit-components";

import { getUsersWithGDPRMinified } from "api/gdpr/get.gdpr.api";
import { setGDPRValue } from "api/gdpr/put.gdpr.api";
import {
    USER_CID,
    USER_FIRST_NAME,
    USER_ID,
    USER_LAST_NAME,
    USER_NICK
} from "api/users/props.users.api";

import useGammaHasAuthority from "common/hooks/use-gamma-has-authority/use-gamma-has-authority";
import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import translations from "./Gdpr.translations.json";

function _generateHeaderTexts(text) {
    const output = {};

    output[USER_CID] = text.Cid;
    output[USER_FIRST_NAME] = text.FirstName;
    output[USER_LAST_NAME] = text.LastName;
    output[USER_NICK] = text.Nick;
    output[USER_ID] = text.Id;
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
        return (
            <DigitLayout.Center size={{ height: "200px" }}>
                <DigitLoading loading />
            </DigitLayout.Center>
        );
    }

    return (
        <DigitSelectMultipleTable
            flex={"1"}
            padding={"0px"}
            size={{ minWidth: "288px", height: "100%" }}
            disableSelectAll
            search
            titleText={text.Users}
            searchText={text.SearchForUsers}
            idProp={USER_ID}
            startOrderByDirection="asc"
            startOrderBy={USER_FIRST_NAME}
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
                                    })[USER_NICK] +
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
                        .catch(error => {
                            queueToast({
                                text: text.SomethingWentWrong
                            });
                        });
                }
            }}
            value={users.filter(user => user.gdpr).map(user => user.id)}
            columnsOrder={[
                USER_CID,
                USER_FIRST_NAME,
                USER_NICK,
                USER_LAST_NAME
            ]}
            headerTexts={_generateHeaderTexts(text)}
            data={users.map(user => {
                return {
                    ...user,
                    __checkbox: user.gdpr
                };
            })}
            startRowsPerPage={25}
        />
    );
};

export default Gdpr;
