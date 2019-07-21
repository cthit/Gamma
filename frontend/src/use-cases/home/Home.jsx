import React from "react";

import { DigitLayout } from "@cthit/react-digit-components";

import UserOptions from "./elements/user-options";
import AdminOptions from "./elements/admin-options";
import WelcomeUser from "./elements/welcome-user";

class Home extends React.Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const { user } = this.props;

        return (
            <DigitLayout.Center>
                <DigitLayout.Padding>
                    <DigitLayout.Column>
                        <WelcomeUser user={user} />
                        <DigitLayout.Spacing />
                        <UserOptions />
                        <DigitLayout.Spacing />
                        <AdminOptions />
                    </DigitLayout.Column>
                </DigitLayout.Padding>
            </DigitLayout.Center>
        );
    }
}

export default Home;
