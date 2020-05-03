import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import { getAuthorityLevel } from "../../../../api/authorities/get.authorities";

const AddToAuthorityLevel = () => {
    const { id } = useParams();

    useEffect(() => {
        if (id == null) {
            return null;
        }

        getAuthorityLevel(id).then(response => {
            console.log(response);
        });
    }, [id]);

    return <div />;
};

export default AddToAuthorityLevel;
