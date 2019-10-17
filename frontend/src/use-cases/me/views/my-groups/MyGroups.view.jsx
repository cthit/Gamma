import React from "react";

import {
    DigitDesign,
    DigitIfElseRendering,
    DigitText,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import styled from "styled-components";
import { Link } from "react-router-dom";
import translations from "./MyGroups.view.translations";

const NoStyleLink = styled(Link)`
    color: black;
`;

class MyGroups extends React.Component {
    componentDidMount() {
        this.props.deltaLoadingFinished();
    }

    render() {
        const { me } = this.props;

        return (
            <DigitIfElseRendering
                test={me != null && me.groups != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.Center>
                                <DigitDesign.Card absWidth={"300px"}>
                                    <DigitDesign.CardTitle
                                        text={text.MyGroups}
                                    />
                                    <DigitDesign.CardBody>
                                        {me.groups.map(group => (
                                            <NoStyleLink
                                                to={"/groups/" + group.id}
                                            >
                                                <DigitText.Text
                                                    text={group.prettyName}
                                                />
                                            </NoStyleLink>
                                        ))}
                                    </DigitDesign.CardBody>
                                </DigitDesign.Card>
                            </DigitLayout.Center>
                        )}
                    />
                )}
            />
        );
    }
}

export default MyGroups;
