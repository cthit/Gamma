import React from "react";

import Save from "@material-ui/icons/Save";

import {
    DigitFAB,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";

const ReviewChanges = ({ groupId, redirectTo, addUserToGroup }) => {
    var savedPostNames = sessionStorage.getItem(groupId + ".postNames");
    if (savedPostNames == null) {
        redirectTo("/groups/" + groupId + "/members");
        return null;
    }

    const members = JSON.parse(savedPostNames).members;

    console.log(members);

    console.log(addUserToGroup);

    return (
        <DigitTranslations
            render={text => (
                <DigitLayout.Center>
                    <DigitLayout.DownRightPosition>
                        <DigitFAB
                            icon={Save}
                            primary
                            onClick={() => {
                                members.forEach(member => {
                                    addUserToGroup(groupId, {
                                        userId: member.id,
                                        post: member.postId,
                                        unofficialName:
                                            member.unofficialPostName
                                    });
                                });

                                sessionStorage.clear();
                            }}
                        />
                    </DigitLayout.DownRightPosition>
                </DigitLayout.Center>
            )}
        />
    );
};

export default ReviewChanges;
