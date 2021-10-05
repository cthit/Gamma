import Save from "@material-ui/icons/Save";
import * as _ from "lodash";
import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";

import {
    DigitLayout,
    DigitDesign,
    useDigitTranslations,
    DigitButton,
    DigitText,
    useDigitToast
} from "@cthit/react-digit-components";

import { removeUserFromGroup } from "api/groups/delete.groups.api";
import { addUserToGroup } from "api/groups/post.groups.api";
import { editUserInGroup, setMembersRequest } from "api/groups/put.groups.api";

import DisplayMembersTable from "common/elements/display-members-table";

import translations from "./ReviewChanges.view.translations";

function getAdditions(previousMembers, newMembers) {
    return newMembers.filter(
        newMember =>
            _.findIndex(previousMembers, ["user.id", newMember.id]) === -1
    );
}

function getDeletions(previousMembers, newMembers) {
    return previousMembers.filter(
        previousMember =>
            _.findIndex(newMembers, ["id", previousMember.id]) === -1
    );
}

function getEdits(previousMembers, newMembers) {
    return _.intersectionWith(
        previousMembers,
        newMembers,
        (previousMember, newMember) => previousMember.id === newMember.id
    );
}

const save = (
    previousMembers,
    newMembersData,
    groupId,
    onFinished,
    queueToast,
    text
) => {
    setMembersRequest(
        groupId,
        newMembersData.map(member => ({
            postId: member.postId,
            userId: member.id,
            unofficialPostName: member.unofficialPostName
        }))
    )
        .then(() => {
            onFinished();
            queueToast({
                text: text.MembersSaved
            });
        })
        .catch(e => {
            queueToast({
                text: text.MembersError
            });
        });
};

const ReviewChanges = ({
    groupName,
    previousMembers,
    groupId,
    posts,
    onFinished,
    newMembersData
}) => {
    const [queueToast] = useDigitToast();
    const [text] = useDigitTranslations(translations);
    const history = useHistory();

    useEffect(() => {
        if (newMembersData == null) {
            history.push("/members/" + groupId);
        }
    }, [newMembersData, history, groupId]);

    return (
        <>
            <DigitDesign.Card
                size={{ minHeight: "min-content" }}
                margin={{ bottom: "8px" }}
            >
                <DigitDesign.CardBody>
                    <DigitLayout.Row
                        margin={"0"}
                        justifyContent={"space-between"}
                        alignItems={"center"}
                        flexWrap={"wrap"}
                    >
                        <DigitText.Heading5
                            text={text.NewMembersForGroup + " " + groupName}
                        />
                        <DigitLayout.Row margin={"0"}>
                            <DigitButton
                                outlined
                                text={text.Back}
                                onClick={() => history.goBack()}
                            />
                            <DigitButton
                                text={text.Save}
                                onClick={() =>
                                    save(
                                        previousMembers,
                                        newMembersData,
                                        groupId,
                                        onFinished,
                                        queueToast,
                                        text
                                    )
                                }
                                startIcon={<Save />}
                                raised
                                primary
                            />
                        </DigitLayout.Row>
                    </DigitLayout.Row>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
            <DisplayMembersTable
                users={newMembersData.map(member => ({
                    ...member,
                    post: _.find(posts, { id: member.postId }),
                    unofficialPostName: member.unofficialPostName
                }))}
                noUsersText={text.NoUsers}
            />
        </>
    );
};

export default ReviewChanges;
