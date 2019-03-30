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

function getInitialValues(currentMembers, members, groupId) {
    const savedPostNames = sessionStorage.getItem(groupId + ".postNames");
    if (savedPostNames != null) {
        return {
            members: JSON.parse(savedPostNames).members
        };
    }

    const necessaryMembersData = members.map(member => {
        const necessaryMemberData = {};
        necessaryMemberData[FIRST_NAME] = member[FIRST_NAME];
        necessaryMemberData[LAST_NAME] = member[LAST_NAME];
        necessaryMemberData[NICK] = member[NICK];
        necessaryMemberData[CID] = member[CID];
        necessaryMemberData[ACCEPTANCE_YEAR] = member[ACCEPTANCE_YEAR];
        necessaryMemberData[ID] = member[ID];

        const previousMemberData = _.find(currentMembers, { id: member.id });

        if (previousMemberData != null) {
            necessaryMemberData.postId = previousMemberData.post.id;
            necessaryMemberData.unofficialPostName =
                previousMemberData.unofficialPostName;
        }

        return necessaryMemberData;
    });

    return {
        members: necessaryMembersData
    };
}

const SetPostNames = ({
    groupId,
    posts,
    currentMembers,
    members,
    onNewMembers
}) => (
    <DigitLayout.Center>
        {console.log(members.filter(members => members.post != null))}
        <DigitEditData
            isInitialValid={
                currentMembers.filter(
                    currentMember => currentMember.post != null
                ).length === members.length
            }
            absWidth={"650px"}
            onSubmit={onNewMembers}
            keysOrder={["members"]}
            initialValues={getInitialValues(currentMembers, members, groupId)}
            keysComponentData={{
                members: {
                    component: NewMembershipArray,
                    componentProps: {
                        posts,
                        currentMembers,
                        members,
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
