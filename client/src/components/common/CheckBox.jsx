import { Switch } from '@headlessui/react';
import { AiOutlineCheck } from 'react-icons/ai';

const Checkbox = ({ label, name, checked, onChange, disabled }) => {
  return (
    <Switch.Group>
      <div className='flex items-center'>
        <Switch
          checked={checked}
          onChange={onChange}
          name={name}
          disabled={disabled}
          className={`relative flex h-5 w-5 items-center justify-center rounded outline-none ring-1 transition-all duration-200 ${!checked && !disabled ? 'bg-white ring-gray-300' : ''} ${checked && !disabled ? 'bg-teal-400 ring-teal-400' : ''} ${disabled ? 'bg-gray-200 ring-gray-200' : ''} `}>
          <AiOutlineCheck
            size='0.75rem'
            className={` ${checked ? 'scale-100' : 'scale-0'} ${checked && !disabled ? 'text-white' : 'text-gray-400'} transition-transform duration-200 ease-out`}
          />
        </Switch>
        <Switch.Label className='ml-2'>{label}</Switch.Label>
      </div>
    </Switch.Group>
  );
};

export default Checkbox;
