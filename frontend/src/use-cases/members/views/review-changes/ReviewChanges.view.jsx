import React from "react";
import Save from "@material-ui/icons/Save";
import {
    DigitLayout,
    DigitDesign,
    useDigitTranslations,
    DigitButton,
    DigitText,
    useDigitToast
} from "@cthit/react-digit-components";
import * as _ from "lodash";
import translations from "./ReviewChanges.view.translations";
import { editUserInGroup } from "../../../../api/groups/put.groups.api";
import { removeUserFromGroup } from "../../../../api/groups/delete.groups.api";
import { addUserToGroup } from "../../../../api/groups/post.groups.api";
import { useHistory } from "react-router-dom";
import DisplayMembersTable from "../../../../common/elements/display-members-table";
import { on401 } from "../../../../common/utils/error-handling/error-handling";

function getAdditions(previousMembers, newMembers) {
    return newMembers.filter(
        newMember => _.findIndex(previousMembers, ["id", newMember.id]) === -1
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
    const additions = getAdditions(previousMembers, newMembersData).map(
        member =>
            addUserToGroup(groupId, {
                userId: member.id,
                post: member.postId,
                unofficialName: member.unofficialPostName
            })
    );

    const deletions = getDeletions(
        previousMembers,
        newMembersData
    ).map(previousMember => removeUserFromGroup(groupId, previousMember.id));

    const edits = getEdits(previousMembers, newMembersData).map(member => {
        const newMemberData = _.find(newMembersData, { id: member.id });

        return editUserInGroup(groupId, member.id, {
            userId: newMemberData.id,
            post: newMemberData.postId,
            unofficialName: newMemberData.unofficialPostName
        });
    });

    Promise.all([...additions, ...deletions, ...edits])
        .then(() => {
            onFinished();
            queueToast({
                text: text.MembersSaved
            });
        })
        .catch(e => {
            if (e != null && e.response.status === 401) {
                on401();
            }
            queueToast({
                text: text.MembersError
            });
            console.log(e);
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

    return (
        <>
            <DigitDesign.Card margin={{ bottom: "16px" }}>
                <DigitDesign.CardBody>
                    <DigitLayout.Row
                        justifyContent={"space-between"}
                        alignItems={"center"}
                        flexWrap={"wrap"}
                    >
                        <DigitText.Heading5
                            text={text.NewMembersForGroup + " " + groupName}
                        />
                        <DigitLayout.Row>
                            <DigitButton
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
