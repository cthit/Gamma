import React from "react";

import {
    DigitStepper,
    DigitTranslations,
    DigitComponentSelector
} from "@cthit/react-digit-components";
import SelectMembers from "./views/select-members";
import SetPostNames from "./views/set-post-names";

import translations from "./EditUsersInGroup.screen.translations";

class EditUsersInGroup extends React.Component {
    state = {
        activeStep: 0
    };

    constructor(props) {
        super(props);

        const {
            loadUsers,
            getGroup,
            groupId,
            gammaLoadingFinished
        } = this.props;

        Promise.all([loadUsers(), getGroup(groupId)]).then(result => {
            gammaLoadingFinished();
        });
    }

    render() {
        const { activeStep } = this.state;
        const { groupId, group, users } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                render={text => (
                    <React.Fragment>
                        <DigitStepper
                            activeStep={activeStep}
                            steps={[
                                { text: text.SelectMembers },
                                { text: text.SetPostNames }
                            ]}
                        />
                        <DigitComponentSelector
                            activeComponent={activeStep}
                            components={[
                                () => (
                                    <SelectMembers
                                        group={group}
                                        users={users}
                                        groupId={groupId}
                                    />
                                ),
                                () => <SetPostNames />
                            ]}
                        />
                    </React.Fragment>
                )}
            />
        );
    }
}

export default EditUsersInGroup;
