import React from "react";

import { DigitEditData, DigitLayout } from "@cthit/react-digit-components";
import NewMembershipArray from "./sub-views/new-membership-array";
import {
    ACCEPTANCE_YEAR,
    CID,
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../../../../../api/users/props.users.api";
import _ from "lodash";

import * as yup from "yup";

function getSelectedMemberIds(groupId) {
    const selectedMemberIds = sessionStorage.getItem(
        groupId + ".selectedMembers"
    );
    if (selectedMemberIds != null) {
        return JSON.parse(selectedMemberIds);
    }
    return [];
}

function getSavedPostNames(groupId) {
    const savedPostNames = sessionStorage.getItem(groupId + ".postNames");
    if (savedPostNames != null) {
        return {
            members: JSON.parse(savedPostNames).members
        };
    }
    return null;
}

function getInitialValues(currentMembers, users, groupId) {
    const selectedMemberIds = getSelectedMemberIds(groupId);
    const savedPostNames = getSavedPostNames(groupId);

    if (savedPostNames != null) {
        return savedPostNames;
    }

    const necessaryMembersData = selectedMemberIds.map(selectedMember => {
        const user = _.find(users, { id: selectedMember });

        const necessaryMemberData = {};
        necessaryMemberData[FIRST_NAME] = user[FIRST_NAME];
        necessaryMemberData[LAST_NAME] = user[LAST_NAME];
        necessaryMemberData[NICK] = user[NICK];
        necessaryMemberData[CID] = user[CID];
        necessaryMemberData[ACCEPTANCE_YEAR] = user[ACCEPTANCE_YEAR];
        necessaryMemberData[ID] = user[ID];

        const previousMemberData = _.find(currentMembers, { id: user.id });

        if (previousMemberData != null) {
            necessaryMemberData.postId = previousMemberData.post.id;
            necessaryMemberData.unofficialPostName =
                previousMemberData.unofficialPostName;
        }

        return necessaryMemberData;
    });

    sessionStorage.setItem(
        groupId + ".postNames",
        JSON.stringify({
            members: necessaryMembersData
        })
    );

    return {
        members: necessaryMembersData
    };
}

const SetPostNames = ({
    groupId,
    posts,
    currentMembers,
    users,
    onNewMembers
}) => (
    <DigitLayout.Center>
        <DigitEditData
            absWidth={"650px"}
            onSubmit={onNewMembers}
            keysOrder={["members"]}
            initialValues={getInitialValues(currentMembers, users, groupId)}
            validationSchema={yup.object().shape({
                members: yup
                    .array(
                        yup
                            .object()
                            .shape({
                                postId: yup.string().required(),
                                unofficialPostName: yup.string().required()
                            })
                            .required()
                    )
                    .required()
            })}
            keysComponentData={{
                members: {
                    component: NewMembershipArray,
                    componentProps: {
                        posts,
                        currentMembers,
                        groupId
                    },
                    array: true
                }
            }}
            titleText={""}
            submitText={"Nästa"}
        />
    </DigitLayout.Center>
);

export default SetPostNames;
