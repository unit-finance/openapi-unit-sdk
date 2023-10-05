# frozen_string_literal: true

require 'openapi_client'
require 'rspec'

module SpecHelper
  attr_reader :configuration

  def configure_tests
    stub_const('FULL_NAME', OpenapiClient::FullName.new(first: 'John', last: 'Doe'))
    stub_const('EMAIL', 'jone.doe@unit-finance.com')
    stub_const('DATE_OF_BIRTH', '1980-08-10')
    stub_const('ADDRESS',
               OpenapiClient::Address.new(street: '123 Main St', city: 'San Francisco', state: 'CA', postal_code: '94205',
                                          country: 'US'))
    stub_const('PHONE', OpenapiClient::Phone.new(country_code: '380', number: '555123222'))
    stub_const('SSN', '123456789')
    stub_const('PASSPORT', '123456789')
    stub_const('LIMITS', OpenapiClient::CardLevelLimits.new(daily_withdrawal: 100, monthly_withdrawal: 1000))
    stub_const('UNIT_TOKEN', ENV['UNIT_TOKEN'])

    @configuration = OpenapiClient::Configuration.new.tap do |config|
      config.access_token = UNIT_TOKEN
      access_t = config.access_token
      config.api_key['Authorization'] = access_t
      config.api_key_prefix['Authorization'] = 'Bearer'
    end
  end

  def get_document_contents
    file = File.open('./spec/test2.pdf', 'rb')
    contents = file.read
    file.close
    contents
  end

  def get_image_contents(file_name)
    file = File.open(file_name, 'rb')
    contents = file.read
    file.close
    contents
  end
end

# See http://rubydoc.info/gems/rspec-core/RSpec/Core/Configuration
RSpec.configure do |config|
  # Enable flags like --only-failures and --next-failure
  config.example_status_persistence_file_path = '.rspec_status'

  # Disable RSpec exposing methods globally on `Module` and `main`
  config.disable_monkey_patching!

  config.expect_with :rspec do |c|
    c.syntax = :expect
  end

  config.include SpecHelper
end
