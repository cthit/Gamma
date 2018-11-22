import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
import GroupForm from "../common-views/group-form";

const EditGroupDetails = ({ group, groupsChange, match }) => (
    <DigitLayout.Center>
        <GroupForm
            onSubmit={(values, actions) => {
                groupsChange(values, match.params.id);
            }}
            initialValues={group}
        />
    </DigitLayout.Center>
);

export default EditGroupDetails;
